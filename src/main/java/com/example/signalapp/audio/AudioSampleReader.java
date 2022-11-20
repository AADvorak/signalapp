package com.example.signalapp.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

public class AudioSampleReader {

    private final AudioInputStream audioInputStream;
    private final AudioFormat format;

    public AudioSampleReader(InputStream inputStream)
            throws UnsupportedAudioFileException, IOException {
        audioInputStream = AudioSystem.getAudioInputStream(inputStream);
        format = audioInputStream.getFormat();
    }

    // Return audio format, and through it, most properties of
    // the audio file: sample size, sample rate, etc.
    public AudioFormat getFormat() {
        return format;
    }

    // Return the number of samples of all channels
    public long getSampleCount() {
        long total = (audioInputStream.getFrameLength() *
                format.getFrameSize() * 8) / format.getSampleSizeInBits();
        return total / format.getChannels();
    }

    // Get the intervealed decoded samples for all channels, from sample
    // index begin (included) to sample index end (excluded) and copy
    // them into samples. end must not exceed getSampleCount(), and the
    // number of samples must not be so large that the associated byte
    // array cannot be allocated
    public void getInterleavedSamples(long begin, long end,
                                      double[] samples) throws IOException,
            IllegalArgumentException {
        long nbSamples = end - begin;
        // nbBytes = nbSamples * sampleSizeinByte * nbChannels
        long nbBytes = nbSamples * (format.getSampleSizeInBits() / 8) *
                format.getChannels();
        if (nbBytes > Integer.MAX_VALUE)
            throw new IllegalArgumentException("too many samples");
        // allocate a byte buffer
        byte[] inBuffer = new byte[(int)nbBytes];
        // read bytes from audio file
        audioInputStream.read(inBuffer, 0, inBuffer.length);
        // decode bytes into samples. Supported encodings are:
        // PCM-SIGNED, PCM-UNSIGNED, A-LAW, U-LAW
        decodeBytes(inBuffer, samples);
    }

    // Extract samples of a particular channel from interleavedSamples and
    // copy them into channelSamples
    public void getChannelSamples(int channel,
                                  double[] interleavedSamples, double[] channelSamples) {
        int nbChannels = format.getChannels();
        for (int i = 0; i < channelSamples.length; i++) {
            channelSamples[i] = interleavedSamples[nbChannels*i + channel];
        }
    }

    // Convenience method. Extract left and right channels for common stereo
    // files. leftSamples and rightSamples must be of size getSampleCount()
    public void getStereoSamples(double[] leftSamples, double[] rightSamples)
            throws IOException {
        long sampleCount = getSampleCount();
        double[] interleavedSamples = new double[(int)sampleCount*2];
        getInterleavedSamples(0, sampleCount, interleavedSamples);
        for (int i = 0; i < leftSamples.length; i++) {
            leftSamples[i] = interleavedSamples[2*i];
            rightSamples[i] = interleavedSamples[2*i+1];
        }
    }

    public void getMonoSamples(double[] samples)
            throws IOException {
        long sampleCount = getSampleCount();
        double[] interleavedSamples = new double[(int)sampleCount];
        getInterleavedSamples(0, sampleCount, interleavedSamples);
        for (int i = 0; i < samples.length; i++) {
            samples[i] = interleavedSamples[i];
        }
    }

    // Private. Decode bytes of audioBytes into audioSamples
    private void decodeBytes(byte[] audioBytes, double[] audioSamples) {
        AudioBytesCoder.decode(audioBytes, audioSamples, format);
    }

}
