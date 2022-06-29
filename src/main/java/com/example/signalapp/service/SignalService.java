package com.example.signalapp.service;

import com.example.signalapp.ApplicationProperties;
import com.example.signalapp.dto.response.IdDtoResponse;
import com.example.signalapp.dto.SignalDataDto;
import com.example.signalapp.dto.request.SignalDtoRequest;
import com.example.signalapp.dto.response.SignalDtoResponse;
import com.example.signalapp.error.SignalAppDataErrorCode;
import com.example.signalapp.error.SignalAppDataException;
import com.example.signalapp.error.SignalAppUnauthorizedException;
import com.example.signalapp.mapper.SignalDataMapper;
import com.example.signalapp.mapper.SignalMapper;
import com.example.signalapp.model.Signal;
import com.example.signalapp.repository.SignalRepository;
import com.example.signalapp.repository.UserTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void delete(String token, int id) throws SignalAppUnauthorizedException {
        signalRepository.deleteByIdAndUserId(id, getUserByToken(token).getId());
    }

    public List<SignalDataDto> getData(String token, int id) throws SignalAppDataException, SignalAppUnauthorizedException {
        Signal signal = signalRepository.findByIdAndUserId(id, getUserByToken(token).getId());
        if (signal == null) {
            throw new SignalAppDataException(SignalAppDataErrorCode.SIGNAL_DOES_NOT_EXIST);
        }
        return signal.getData().stream().map(SignalDataMapper.INSTANCE::signalDataToDto).collect(Collectors.toList());
    }

}
