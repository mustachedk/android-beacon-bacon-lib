package dk.mustache.beaconbacon.datamodels;

import android.graphics.Typeface;

public class BBConfigurationObject {
    private Typeface typeface;
    private int tintColor = -1;

    public BBConfigurationObject(Typeface typeface, int tintColor) {
        this.typeface = typeface;
        this.tintColor = tintColor;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public int getTintColor() {
        return tintColor;
    }

    public void setTintColor(int tintColor) {
        this.tintColor = tintColor;
    }
}
