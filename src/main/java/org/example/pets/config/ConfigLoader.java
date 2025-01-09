package org.example.pets.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final String DEFAULT_PROPERTIES_PATH = "db.properties";
    private Properties properties;

    public ConfigLoader() {
        this(DEFAULT_PROPERTIES_PATH);
    }

    public ConfigLoader(String path) {
        properties = new Properties();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new RuntimeException("Файл " + path + " не найден");
            }

            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getProperties(String key) {
        return properties.getProperty(key);
    }
}
