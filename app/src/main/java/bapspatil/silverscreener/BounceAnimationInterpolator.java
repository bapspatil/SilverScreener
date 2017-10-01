package bapspatil.silverscreener;

import android.view.animation.Interpolator;

public class BounceAnimationInterpolator implements Interpolator {

    private double mAmplitude = 1;
    private double mFrequency = 10;

    public BounceAnimationInterpolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    @Override
    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
                Math.cos(mFrequency * time) + 1);
    }
}
