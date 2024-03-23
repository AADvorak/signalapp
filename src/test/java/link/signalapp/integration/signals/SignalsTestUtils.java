package link.signalapp.integration.signals;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class SignalsTestUtils {

    public static byte[] generateWav(int length, int channels, float sampleRate) throws IOException {
        float[] buffer = new float[length];
        for (int sample = 0; sample < buffer.length; sample++) {
            buffer[sample] = new Random().nextFloat();
        }
        final byte[] byteBuffer = new byte[buffer.length * 2];
        int bufferIndex = 0;
        for (int i = 0; i < byteBuffer.length; i++) {
            final int x = (int)(buffer[bufferIndex++] * 32767.0);
            byteBuffer[i++] = (byte)x;
            byteBuffer[i] = (byte)(x >>> 8);
        }
        final boolean bigEndian = false;
        final boolean signed = true;
        final int bits = 16;
        AudioFormat format = new AudioFormat(sampleRate, bits, channels, signed, bigEndian);
        ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format, buffer.length);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, baos);
        return baos.toByteArray();
    }
}
