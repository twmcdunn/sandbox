package org.delightofcomposition.synth;

import java.nio.file.Paths;
import java.util.Arrays;

import org.delightofcomposition.sound.FFT2;
import org.delightofcomposition.sound.ReadSound;

public class SimpleSynth extends Synth {
    public double origFreq;
    double[] wetSig;
    double[] sample;

    public SimpleSynth() {
        origFreq = 394;
        double[] cathedral = ReadSound.readSoundDoubles("resources/cathedral.wav");
        sample = ReadSound.readSoundDoubles("resources/4.wav");

        sample = Arrays.copyOf(sample, sample.length + cathedral.length);
        cathedral = Arrays.copyOf(cathedral, sample.length);
        wetSig = FFT2.convAsImaginaryProduct(sample, cathedral);
        wetSig = Arrays.copyOf(wetSig, sample.length);
        double sMax = 0;
        double wMax = 0;
        for (int i = 0; i < wetSig.length; i++) {
            sMax = Math.max(sMax, Math.abs(sample[i]));
            wMax = Math.max(wMax, Math.abs(wetSig[i]));
        }
        for (int i = 0; i < wetSig.length; i++) {
            sample[i] /= sMax;
            wetSig[i] /= wMax;
        }

    }

    public double[] reverb(double amp) {
        double[] sig = new double[wetSig.length];
        for (int i = 0; i < sig.length; i++) {
            sig[i] = amp * sample[i] + (1 - amp) * wetSig[i];
        }
        return sig;
    }

    public double[] synthAlg(double freq, double amp) {
        double[] reverb = reverb(amp);// amp dependent reverb

        double[] processed = new double[(int) (reverb.length * origFreq / freq)];

        for (int i = 0; i < processed.length && i < processed.length; i++) {
            double exInd = i * freq / origFreq;
            int index = (int) exInd;
            double fract = exInd - index;
            double frame1 = reverb[index];
            double frame2 = frame1;
            if (index + 1 < reverb.length)
                frame2 = reverb[index + 1];
            double frame = frame1 * (1 - fract) + frame2 * fract;
            frame *= amp;
            processed[i] += frame;
        }
        return processed;
    }
}
