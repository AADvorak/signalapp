package com.example.signalapp.service;

import com.example.signalapp.ApplicationProperties;
import com.example.signalapp.audio.AudioBytesCoder;
import com.example.signalapp.audio.AudioSampleReader;
import com.example.signalapp.dto.response.IdDtoResponse;
import com.example.signalapp.dto.request.SignalDtoRequest;
import com.example.signalapp.dto.response.ResponseWithTotalCounts;
import com.example.signalapp.dto.response.SignalDtoResponse;
import com.example.signalapp.dto.response.SignalWithDataDtoResponse;
import com.example.signalapp.error.*;
import com.example.signalapp.file.FileManager;
import com.example.signalapp.mapper.SignalMapper;
import com.example.signalapp.model.Signal;
import com.example.signalapp.model.User;
import com.example.signalapp.repository.SignalRepository;
import com.example.signalapp.repository.UserTokenRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class SignalService extends ServiceBase {

    public static final int MAX_SIGNAL_LENGTH = 1024000;
    public static final int MAX_USER_STORED_SIGNALS_NUMBER = 50;

    private final SignalRepository signalRepository;
    private final FileManager fileManager;

    public SignalService(UserTokenRepository userTokenRepository, ApplicationProperties applicationProperties,
                         SignalRepository signalRepository, FileManager fileManager) {
        super(userTokenRepository, applicationProperties);
        this.signalRepository = signalRepository;
        this.fileManager = fileManager;
    }

    public ResponseWithTotalCounts<SignalDtoResponse> get(String token, String filter, int page, int size,
                                                          String sortBy, String sortDir)
            throws SignalAppUnauthorizedException {
        int userId = getUserByToken(token).getId();
        Pageable pageable = PageRequest.of(page, size > MAX_USER_STORED_SIGNALS_NUMBER || size <= 0
                ? MAX_USER_STORED_SIGNALS_NUMBER : size, getSort(sortBy, sortDir, filter));
        Page<Signal> signalPage = filter != null && !filter.isEmpty()
                ? signalRepository.findByUserIdAndFilter(userId, pageable, "%" + filter + "%")
                : signalRepository.findByUserId(userId, pageable);
        return new ResponseWithTotalCounts<SignalDtoResponse>()
                .setData(signalPage.stream().map(SignalMapper.INSTANCE::signalToDto).toList())
                .setPages(signalPage.getTotalPages())
                .setElements(signalPage.getTotalElements());
    }

    @Transactional(rollbackFor = IOException.class)
    public IdDtoResponse add(String token, SignalDtoRequest request) throws SignalAppUnauthorizedException,
            IOException, SignalAppConflictException {
        int userId = getUserByToken(token).getId();
        checkStoredByUserSignalsNumber(userId);
        Signal signal = signalRepository.save(new Signal(request, userId));
        writeSignalDataToWavFile(signal, request.getData());
        return new IdDtoResponse().setId(signal.getId());
    }

    @Transactional(rollbackFor = IOException.class)
    public void update(String token, SignalDtoRequest request, int id) throws SignalAppUnauthorizedException,
            IOException, SignalAppNotFoundException {
        Signal signal = getSignalByUserTokenAndId(token, id)
                .setName(request.getName())
                .setDescription(request.getDescription())
                .setMaxAbsY(request.getMaxAbsY())
                .setSampleRate(request.getSampleRate())
                .setXMin(request.getXMin());
        signalRepository.save(signal);
        writeSignalDataToWavFile(signal, request.getData());
    }

    @Transactional
    public void delete(String token, int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        int userId = getUserByToken(token).getId();
        if (signalRepository.deleteByIdAndUserId(id, userId) == 0) {
            throw new SignalAppNotFoundException();
        } else {
            fileManager.deleteSignalData(userId, id);
        }
    }

    public SignalWithDataDtoResponse getSignalWithData(String token, int id) throws SignalAppNotFoundException,
            SignalAppUnauthorizedException, UnsupportedAudioFileException, IOException {
        Signal signal = getSignalByUserTokenAndId(token, id);
        SignalWithDataDtoResponse response = SignalMapper.INSTANCE.signalToDtoWithData(signal);
        response.setData(getDataForSignal(signal));
        return response;
    }

    public List<BigDecimal> getData(String token, int id) throws SignalAppUnauthorizedException,
            SignalAppNotFoundException, IOException, UnsupportedAudioFileException {
        return getDataForSignal(getSignalByUserTokenAndId(token,id));
    }

    public byte[] getWav(String token, int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException, IOException {
        Signal signal = getSignalByUserTokenAndId(token, id);
        return fileManager.readWavFromFile(signal.getUserId(), signal.getId());
    }

    @Transactional(rollbackFor = IOException.class)
    public void importWav(String token, String fileName, byte[] data)
            throws SignalAppUnauthorizedException, UnsupportedAudioFileException, IOException, SignalAppException {
        if (data.length > 2 * MAX_SIGNAL_LENGTH) {
            throw new SignalAppException(SignalAppErrorCode.TOO_LONG_FILE,
                    new MaxSizeExceptionParams(2 * SignalService.MAX_SIGNAL_LENGTH));
        }
        User user = getUserByToken(token);
        checkStoredByUserSignalsNumber(user.getId());
        AudioSampleReader asr = new AudioSampleReader(new ByteArrayInputStream(data));
        long sampleCount = asr.getSampleCount();
        if (asr.getFormat().getChannels() == 1) {
            double [] samples = new double[(int)sampleCount];
            asr.getMonoSamples(samples);
            makeAndSaveSignal(fileName, samples, asr.getFormat(), user);
        } else {
            double [] rightSamples = new double[(int)sampleCount];
            double [] leftSamples = new double[(int)sampleCount];
            asr.getStereoSamples(leftSamples, rightSamples);
            makeAndSaveSignal(fileName + " (right)", rightSamples, asr.getFormat(), user);
            makeAndSaveSignal(fileName + " (left)", leftSamples, asr.getFormat(), user);
        }
    }

    private Signal getSignalByUserTokenAndId(String token, int id)
            throws SignalAppNotFoundException, SignalAppUnauthorizedException {
        Signal signal = signalRepository.findByIdAndUserId(id, getUserByToken(token).getId());
        if (signal == null) {
            throw new SignalAppNotFoundException();
        }
        return signal;
    }

    private List<BigDecimal> getDataForSignal(Signal signal) throws IOException, UnsupportedAudioFileException {
        byte[] bytes = fileManager.readWavFromFile(signal.getUserId(), signal.getId());
        AudioSampleReader asr = new AudioSampleReader(new ByteArrayInputStream(bytes));
        double[] samples = new double[(int)asr.getSampleCount() / 2];
        asr.getMonoSamples(samples);
        List<BigDecimal> data = new ArrayList<>();
        for (double sample : samples) {
            data.add(BigDecimal.valueOf(sample).multiply(signal.getMaxAbsY()));
        }
        return data;
    }

    private void makeAndSaveSignal(String fileName, double[] samples, AudioFormat format, User user) throws IOException {
        AudioFormat newFormat = new AudioFormat(format.getSampleRate(),
                format.getSampleSizeInBits(), 1, true, format.isBigEndian());
        Signal signal = new Signal()
                .setName(fileName)
                .setDescription("Imported from file " + fileName)
                .setUserId(user.getId())
                .setMaxAbsY(BigDecimal.ONE)
                .setSampleRate(BigDecimal.valueOf(format.getSampleRate()))
                .setXMin(BigDecimal.ZERO);
        signal = signalRepository.save(signal);
        writeSamplesToWavFile(signal, samples, newFormat);
    }

    private void writeSignalDataToWavFile(Signal signal, List<BigDecimal> data) throws IOException {
        AudioFormat format = new AudioFormat(signal.getSampleRate().floatValue(), 16, 1, true, false);
        double[] samples = new double[data.size()];
        for (int i = 0; i < samples.length; i++) {
            samples[i] = data.get(i).divide(signal.getMaxAbsY(), new MathContext(10, RoundingMode.HALF_DOWN)).doubleValue();
        }
        writeSamplesToWavFile(signal, samples, format);
    }

    private void writeSamplesToWavFile(Signal signal, double[] samples, AudioFormat format) throws IOException {
        int sampleCount = samples.length;
        int numBytes = sampleCount * (format.getSampleSizeInBits() / 8);
        byte[] bytes = new byte[numBytes];
        AudioBytesCoder.encode(samples, bytes, sampleCount, format);
        fileManager.writeBytesToWavFile(signal.getUserId(), signal.getId(), format, bytes);
    }

    private Sort getSort(String sortBy, String sortDir, String filter) {
        String sortColumn = sortBy != null && !sortBy.isEmpty() ? sortBy : "createTime";
        sortColumn = filter == null || filter.isEmpty() ? sortColumn : camelToSnake(sortColumn);
        Sort sort = Sort.by(sortColumn);
        return sortDir != null && sortDir.equals("asc") ? sort : sort.descending();
    }

    private void checkStoredByUserSignalsNumber(int userId) throws SignalAppConflictException {
        if (signalRepository.countByUserId(userId) >= MAX_USER_STORED_SIGNALS_NUMBER) {
            throw new SignalAppConflictException(SignalAppErrorCode.TOO_MANY_SIGNALS_STORED,
                    new MaxNumberExceptionParams(SignalService.MAX_USER_STORED_SIGNALS_NUMBER));
        }
    }

    private String camelToSnake(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        char c = str.charAt(0);
        result.append(Character.toLowerCase(c));
        for (int i = 1; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            }
            else {
                result.append(ch);
            }
        }
        return result.toString();
    }

}
