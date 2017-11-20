package dk.mustache.beaconbacon.datamodels;

public class BBConfig {
    private static final BBConfig ourInstance = new BBConfig();

    public static BBConfig getInstance() {
        return ourInstance;
    }

    private BBConfig() {
    }
}
