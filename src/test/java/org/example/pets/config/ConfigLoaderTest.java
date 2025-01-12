package org.example.pets.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigLoaderTest {
    private Map<String, String> expectedPropertyValues;

    @BeforeEach
    void init() {
        expectedPropertyValues = new HashMap<>();
        expectedPropertyValues.put("org.postgresql.Driver", "driverClassName");
        expectedPropertyValues.put("url", "jdbc:postgresql://localhost:5432/postgres");
        expectedPropertyValues.put("userName", "postgres");
        expectedPropertyValues.put("password", "1");
    }

    @Test
    void shouldCorrectlyLoadConfigProperties() {
        ConfigLoader configLoader = new ConfigLoader("db.properties");
        assertEquals("org.postgresql.Driver", configLoader.getProperties("driverClassName"));
    }

    @Test
    void shouldThrowExceptionIfFileIsAbsent() {
        assertThrows(RuntimeException.class, () -> new ConfigLoader("Absent.File"));
    }
}
