package com.example.signalapp.service;

import com.example.signalapp.dao.SignalDao;
import com.example.signalapp.daoimpl.SignalDaoImpl;
import com.example.signalapp.dto.IdDtoResponse;
import com.example.signalapp.dto.SignalDataDto;
import com.example.signalapp.dto.SignalDtoRequest;
import com.example.signalapp.dto.SignalDtoResponse;
import com.example.signalapp.model.Signal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SignalService {

    SignalDao signalDao;

    public SignalService(SignalDaoImpl signalDao) {
        this.signalDao = signalDao;
    }

    public List<SignalDtoResponse> getAll() {
        return signalDao.getAll().stream().map(signal -> new SignalDtoResponse(signal.getId(),
                signal.getName(), signal.getDescription())).collect(Collectors.toList());
    }

    public IdDtoResponse add(SignalDtoRequest request) {
        return new IdDtoResponse(signalDao.insert(new Signal(request)).getId());
    }

    public void update(SignalDtoRequest request, int id) {
        signalDao.update(new Signal(request), id);
    }

    public void delete(int id) {
        signalDao.delete(id);
    }

    public List<SignalDataDto> getData(int id) {
        return signalDao.getData(id).stream().map(item ->
                new SignalDataDto(item.getX(), item.getY())).collect(Collectors.toList());
    }

}
