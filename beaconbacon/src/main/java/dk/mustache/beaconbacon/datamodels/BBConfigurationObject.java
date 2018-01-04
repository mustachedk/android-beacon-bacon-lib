package dk.mustache.beaconbacon.datamodels;

import android.graphics.Typeface;

import dk.mustache.beaconbacon.api.ApiManager;

public class BBConfigurationObject {
    private Typeface typeface;
    private int tintColor = -1;
    private String baseUrl;
    private String apiKey;

    public BBConfigurationObject(Typeface typeface, int tintColor, String baseUrl, String apiKey) {
        this.typeface = typeface;
        this.tintColor = tintColor;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
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

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = String.format("Bearer %s", apiKey);
    }
}
