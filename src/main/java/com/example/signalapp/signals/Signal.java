/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.signals;

import java.util.List;

/**
 *
 * @author anton
 */
public class Signal {
    
    private SignalTitle title;
    
    private List<SignalData> data;
    
    Signal () {}

    public Signal(SignalTitle title, List<SignalData> data) {
        this.title = title;
        this.data = data;
    }

    public SignalTitle getTitle() {
        return title;
    }

    public void setTitle(SignalTitle title) {
        this.title = title;
    }

    public List<SignalData> getData() {
        return data;
    }

    public void setData(List<SignalData> data) {
        this.data = data;
    }
    
}
