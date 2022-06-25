package com.example.signalapp.service;

import com.example.signalapp.dto.response.IdDtoResponse;
import com.example.signalapp.dto.SignalDataDto;
import com.example.signalapp.dto.request.SignalDtoRequest;
import com.example.signalapp.dto.response.SignalDtoResponse;
import com.example.signalapp.error.SignalAppDataErrorCode;
import com.example.signalapp.error.SignalAppDataException;
import com.example.signalapp.model.Signal;
import com.example.signalapp.repository.SignalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignalService {

    private final SignalRepository signalRepository;

    public List<SignalDtoResponse> getAll() {
        return signalRepository.findAll().stream().map(signal -> new SignalDtoResponse(signal.getId(),
                signal.getName(), signal.getDescription())).collect(Collectors.toList());
    }

    public IdDtoResponse add(SignalDtoRequest request) {
        return new IdDtoResponse(signalRepository.save(new Signal(request)).getId());
    }

    public void update(SignalDtoRequest request, int id) {
        Signal signal = new Signal(request);
        signalRepository.findById(id).map(existingSignal -> {
            existingSignal.setName(signal.getName());
            existingSignal.setDescription(signal.getDescription());
            existingSignal.setData(signal.getData());
            return signalRepository.save(existingSignal);
        }).orElseGet(() -> {
            signal.setId(id);
            return signalRepository.save(signal);
        });
    }

    public void delete(int id) {
        signalRepository.deleteById(id);
    }

    public List<SignalDataDto> getData(int id) throws SignalAppDataException {
        Optional<Signal> optionalSignal = signalRepository.findById(id);
        if (optionalSignal.isEmpty()) {
            throw new SignalAppDataException(SignalAppDataErrorCode.SIGNAL_DOES_NOT_EXIST);
        }
        return optionalSignal.get().getData().stream().map(item ->
                new SignalDataDto(item.getX(), item.getY())).collect(Collectors.toList());
    }

}
