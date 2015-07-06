package io.kairos.maps.context;

/**
 * Encapsulates all configuration in the system. Currently hard-coded.
 * Can later be moved out into a config file.
 */
public class Configuration {
    private static Configuration instance = new Configuration();

    private Configuration() { }

    public static Configuration instance() {
        return instance;
    }
}
