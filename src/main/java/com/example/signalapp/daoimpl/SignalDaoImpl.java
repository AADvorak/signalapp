package com.example.signalapp.daoimpl;

import com.example.signalapp.dao.SignalDao;
import com.example.signalapp.model.Signal;
import com.example.signalapp.model.SignalData;
import com.example.signalapp.repository.SignalRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class SignalDaoImpl implements SignalDao {

    private final SignalRepository signalRepository;

    public SignalDaoImpl(SignalRepository signalRepository) {
        this.signalRepository = signalRepository;
    }

    @Override
    public List<Signal> getAll() {
        return signalRepository.findAll();
    }

    @Override
    public Signal insert(Signal signal) {
        return signalRepository.save(signal);
    }

    @Override
    public Signal update(Signal signal, int id) {
        return signalRepository.findById(id).map(existingSignal -> {
            existingSignal.setName(signal.getName());
            existingSignal.setDescription(signal.getDescription());
            existingSignal.setData(signal.getData());
            return signalRepository.save(existingSignal);
        }).orElseGet(() -> {
            signal.setId(id);
            return signalRepository.save(signal);
        });
    }

    @Override
    public void delete(int id) {
        signalRepository.deleteById(id);
    }

    @Override
    public List<SignalData> getData(int id) {
        Optional<Signal> optionalSignal = signalRepository.findById(id);
        if (optionalSignal.isPresent()) {
            return optionalSignal.get().getData();
        }
        return new ArrayList<>();
    }
}
