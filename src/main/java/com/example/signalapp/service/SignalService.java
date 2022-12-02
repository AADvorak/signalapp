package com.example.signalapp.service;

import com.example.signalapp.ApplicationProperties;
import com.example.signalapp.audio.AudioBytesCoder;
import com.example.signalapp.audio.AudioSampleReader;
import com.example.signalapp.dto.response.IdDtoResponse;
import com.example.signalapp.dto.SignalDataDto;
import com.example.signalapp.dto.request.SignalDtoRequest;
import com.example.signalapp.dto.response.SignalDtoResponse;
import com.example.signalapp.dto.response.SignalWithDataDtoResponse;
import com.example.signalapp.error.*;
import com.example.signalapp.file.FileManager;
import com.example.signalapp.mapper.SignalMapper;
import com.example.signalapp.model.Signal;
import com.example.signalapp.model.User;
import com.example.signalapp.repository.SignalRepository;
import com.example.signalapp.repository.UserTokenRepository;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SignalService extends ServiceBase {

    public static final int MAX_SIGNAL_LENGTH = 1024000;

    private final SignalRepository signalRepository;
    private final FileManager fileManager;

    public SignalService(UserTokenRepository userTokenRepository, ApplicationProperties applicationProperties,
                         SignalRepository signalRepository, FileManager fileManager) {
        super(userTokenRepository, applicationProperties);
        this.signalRepository = signalRepository;
        this.fileManager = fileManager;
    }

    public List<SignalDtoResponse> getAll(String token) throws SignalAppUnauthorizedException {
        return signalRepository.findByUserId(getUserByToken(token).getId()).stream()
                .map(SignalMapper.INSTANCE::signalToDto).collect(Collectors.toList());
    }

    public IdDtoResponse add(String token, SignalDtoRequest request) throws SignalAppUnauthorizedException, IOException {
        BigDecimal maxAbsY = getMaxAbsY(request.getData());
        Signal signal = signalRepository.save(new Signal(request, getUserByToken(token), maxAbsY));
        writeSignalDataToWavFile(signal, request.getData());
        return new IdDtoResponse(signal.getId());
    }

    public void update(String token, SignalDtoRequest request, int id) throws SignalAppUnauthorizedException,
            IOException, SignalAppNotFoundException {
        Signal signal = getSignalByUserTokenAndId(token, id);
        BigDecimal maxAbsY = getMaxAbsY(request.getData());
        signal.setName(request.getName());
        signal.setDescription(request.getDescription());
        signal.setMaxAbsY(maxAbsY);
        writeSignalDataToWavFile(signal, request.getData());
        signalRepository.save(signal);
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

    public List<SignalDataDto> getData(String token, int id) throws SignalAppUnauthorizedException,
            SignalAppNotFoundException, IOException, UnsupportedAudioFileException {
        return getDataForSignal(getSignalByUserTokenAndId(token,id));
    }

    public byte[] getWav(String token, int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException, IOException {
        Signal signal = getSignalByUserTokenAndId(token, id);
        return fileManager.readWavFromFile(signal.getUser().getId(), signal.getId());
    }

    public void importWav(String token, String fileName, byte[] data)
            throws SignalAppUnauthorizedException, UnsupportedAudioFileException, IOException, SignalAppException {
        if (data.length > 2 * MAX_SIGNAL_LENGTH) {
            throw new SignalAppException(SignalAppErrorCode.TOO_LONG_FILE);
        }
        User user = getUserByToken(token);
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

    private List<SignalDataDto> getDataForSignal(Signal signal) throws IOException, UnsupportedAudioFileException {
        byte[] bytes = fileManager.readWavFromFile(signal.getUser().getId(), signal.getId());
        AudioSampleReader asr = new AudioSampleReader(new ByteArrayInputStream(bytes));
        double[] samples = new double[(int)asr.getSampleCount() / 2];
        asr.getMonoSamples(samples);
        List<SignalDataDto> data = new ArrayList<>();
        double step = 1 / asr.getFormat().getFrameRate();
        for (int i = 0; i < samples.length; i++) {
            SignalDataDto point = new SignalDataDto();
            point.setX(BigDecimal.valueOf(i * step));
            point.setY(BigDecimal.valueOf(samples[i]).multiply(signal.getMaxAbsY()));
            data.add(point);
        }
        return data;
    }

    private void makeAndSaveSignal(String fileName, double[] samples, AudioFormat format, User user) throws IOException {
        AudioFormat newFormat = new AudioFormat(format.getSampleRate(),
                format.getSampleSizeInBits(), 1, true, format.isBigEndian());
        Signal signal = new Signal();
        signal.setName(fileName);
        signal.setDescription("Imported from file " + fileName);
        signal.setUser(user);
        signal.setMaxAbsY(BigDecimal.ONE);
        signal = signalRepository.save(signal);
        writeSamplesToWavFile(signal, samples, newFormat);
    }

    private void writeSignalDataToWavFile(Signal signal, List<SignalDataDto> data) throws IOException {
        // todo check signal format
        MathContext divideMc = new MathContext(10, RoundingMode.HALF_DOWN);
        float sampleRate = Math.round(BigDecimal.ONE.divide(data.get(1).getX().subtract(data.get(0).getX()), divideMc)
                .round(new MathContext(0, RoundingMode.HALF_UP)).floatValue());
        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        double[] samples = new double[data.size()];
        for (int i = 0; i < samples.length; i++) {
            samples[i] = data.get(i).getY().divide(signal.getMaxAbsY(), divideMc).doubleValue();
        }
        writeSamplesToWavFile(signal, samples, format);
    }

    private void writeSamplesToWavFile(Signal signal, double[] samples, AudioFormat format) throws IOException {
        int sampleCount = samples.length;
        int numBytes = sampleCount * (format.getSampleSizeInBits() / 8);
        byte[] bytes = new byte[numBytes];
        AudioBytesCoder.encode(samples, bytes, sampleCount, format);
        fileManager.writeBytesToWavFile(signal.getUser().getId(), signal.getId(), format, bytes);
    }

    private BigDecimal getMaxAbsY(List<SignalDataDto> data) {
        return data.stream().map(point -> point.getY().abs())
                .max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
    }

}
