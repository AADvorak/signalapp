package com.example.signalapp.audio;

import javax.sound.sampled.AudioFormat;

public class AudioBytesCoder {

    public static void decode(byte[] audioBytes, double[] audioSamples, AudioFormat format) {
        int sampleSizeInBytes = format.getSampleSizeInBits() / 8;
        int[] sampleBytes = new int[sampleSizeInBytes];
        int k = 0; // index in audioBytes
        for (int i = 0; i < audioSamples.length; i++) {
            // collect sample byte in big-endian order
            if (format.isBigEndian()) {
                // bytes start with MSB
                for (int j = 0; j < sampleSizeInBytes; j++) {
                    sampleBytes[j] = audioBytes[k++];
                }
            } else {
                // bytes start with LSB
                for (int j = sampleSizeInBytes - 1; j >= 0; j--) {
                    sampleBytes[j] = audioBytes[k++];
                    if (sampleBytes[j] != 0)
                        j = j + 0;
                }
            }
            // get integer value from bytes
            int ival = 0;
            for (int j = 0; j < sampleSizeInBytes; j++) {
                ival += sampleBytes[j];
                if (j < sampleSizeInBytes - 1) ival <<= 8;
            }
            // decode value
            double ratio = Math.pow(2., format.getSampleSizeInBits() - 1);
            double val = ((double) ival) / ratio;
            audioSamples[i] = val;
        }
    }

    public static void encode(double[] audioData, byte[] audioBytes,
                              int length, AudioFormat format) {
        int in;
        if (format.getSampleSizeInBits() == 16) {
            if (format.isBigEndian()) {
                for (int i = 0; i < length; i++) {
                    in = (int)(audioData[i]*32767);
                    /* First byte is MSB (high order) */
                    audioBytes[2*i] = (byte)(in >> 8);
                    /* Second byte is LSB (low order) */
                    audioBytes[2*i+1] = (byte)(in & 255);
                }
            } else {
                for (int i = 0; i < length; i++) {
                    in = (int)(audioData[i]*32767);
                    /* First byte is LSB (low order) */
                    audioBytes[2*i] = (byte)(in & 255);
                    /* Second byte is MSB (high order) */
                    audioBytes[2*i+1] = (byte)(in >> 8);
                }
            }
        } else if (format.getSampleSizeInBits() == 8) {
            if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
                for (int i = 0; i < length; i++) {
                    audioBytes[i] = (byte)(audioData[i]*127);
                }
            } else {
                for (int i = 0; i < length; i++) {
                    audioBytes[i] = (byte)(audioData[i]*127 + 127);
                }
            }
        }
    }

}
