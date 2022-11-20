package com.example.signalapp.service;

import com.example.signalapp.ApplicationProperties;
import com.example.signalapp.audio.AudioSampleReader;
import com.example.signalapp.dto.response.IdDtoResponse;
import com.example.signalapp.dto.SignalDataDto;
import com.example.signalapp.dto.request.SignalDtoRequest;
import com.example.signalapp.dto.response.SignalDtoResponse;
import com.example.signalapp.error.SignalAppDataErrorCode;
import com.example.signalapp.error.SignalAppDataException;
import com.example.signalapp.error.SignalAppNotFoundException;
import com.example.signalapp.error.SignalAppUnauthorizedException;
import com.example.signalapp.mapper.SignalDataMapper;
import com.example.signalapp.mapper.SignalMapper;
import com.example.signalapp.model.Signal;
import com.example.signalapp.model.SignalData;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SignalService extends ServiceBase {

    private final SignalRepository signalRepository;

    public SignalService(UserTokenRepository userTokenRepository, ApplicationProperties applicationProperties, SignalRepository signalRepository) {
        super(userTokenRepository, applicationProperties);
        this.signalRepository = signalRepository;
    }

    public List<SignalDtoResponse> getAll(String token) throws SignalAppUnauthorizedException {
        return signalRepository.findByUserId(getUserByToken(token).getId()).stream()
                .map(SignalMapper.INSTANCE::signalToDto).collect(Collectors.toList());
    }

    public IdDtoResponse add(String token, SignalDtoRequest request) throws SignalAppUnauthorizedException {
        return new IdDtoResponse(signalRepository.save(new Signal(request, getUserByToken(token))).getId());
    }

    public void update(String token, SignalDtoRequest request, int id) throws SignalAppUnauthorizedException, SignalAppDataException {
        Signal signal = signalRepository.findByIdAndUserId(id, getUserByToken(token).getId());
        if (signal == null) {
            throw new SignalAppDataException(SignalAppDataErrorCode.SIGNAL_DOES_NOT_EXIST);
        }
        signal.setName(request.getName());
        signal.setDescription(request.getDescription());
        signal.setData(request.getData().stream().map(SignalDataMapper.INSTANCE::dtoToSignalData).collect(Collectors.toList()));
        signalRepository.save(signal);
    }

    @Transactional
    public void delete(String token, int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        if (signalRepository.deleteByIdAndUserId(id, getUserByToken(token).getId()) == 0) {
            throw new SignalAppNotFoundException();
        }
    }

    public List<SignalDataDto> getData(String token, int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        Signal signal = signalRepository.findByIdAndUserId(id, getUserByToken(token).getId());
        if (signal == null) {
            throw new SignalAppNotFoundException();
        }
        return signal.getData().stream().map(SignalDataMapper.INSTANCE::signalDataToDto).collect(Collectors.toList());
    }

    public void importWav(String token, String fileName, byte[] data)
            throws SignalAppUnauthorizedException, UnsupportedAudioFileException, IOException {
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

    private void makeAndSaveSignal(String fileName, double[] samples, AudioFormat format, User user) {
        Signal signal = new Signal();
        signal.setName(fileName);
        signal.setDescription("Imported from file " + fileName);
        signal.setUser(user);
        List<SignalData> data = new ArrayList<>();
        double step = 1 / format.getFrameRate();
        for (int i = 0; i < samples.length; i++) {
            SignalData point = new SignalData();
            point.setX(BigDecimal.valueOf(i * step));
            point.setY(BigDecimal.valueOf(samples[i]));
            data.add(point);
        }
        signal.setData(data);
        signalRepository.save(signal);
    }

}
