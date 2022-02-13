package com.example.signalapp.dao;

import com.example.signalapp.model.Signal;
import com.example.signalapp.model.SignalData;

import java.util.List;

public interface SignalDao {

    List<Signal> getAll();

    Signal insert (Signal signal);

    Signal update (Signal signal, int id);

    void delete(int id);

    List<SignalData> getData(int id);

}
