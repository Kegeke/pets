package org.example.pets.servlet.dto;


import org.example.pets.model.Owner;
import org.example.pets.model.Pet;
import org.example.pets.servlet.mapper.PetDtoMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PetDtoMapTest {
    private static Map<String, Object> fieldValues;

    private static PetDtoMap dtoMap;

    @BeforeAll
    static void init() {
        fieldValues = new HashMap<>();
        fieldValues.put("id", 1);
        fieldValues.put("name", "Rikki");
        fieldValues.put("age", 2);
        fieldValues.put("ownerId", 2);
        fieldValues.put("ownerName", "David");

        dtoMap = new PetDtoMap();
    }

    @Test
    void shouldCorrectlyMapToOutgoingDto() {
        Owner owner = new Owner((Integer) fieldValues.get("ownerId"),
                (String) fieldValues.get("ownerName"), null);
        Pet pet = new Pet((Integer) fieldValues.get("id"), (String) fieldValues.get("name"),
                (Integer) fieldValues.get("age"), owner);
        PetOutDto petOutDto = dtoMap.map(pet);
        assertEquals(fieldValues.get("id"), petOutDto.getId());
        assertEquals(fieldValues.get("name"), petOutDto.getName());
        assertEquals(fieldValues.get("age"), petOutDto.getAge());
        assertEquals(fieldValues.get("ownerId"), petOutDto.getOwnerId());
        assertEquals(fieldValues.get("ownerName"), petOutDto.getOwnerName());
    }

    @Test
    void shouldCorrectlyMapToOutgoingDtoWithoutOwnerField() {
        Pet pet = new Pet();
        pet.setId((Integer) fieldValues.get("id"));
        pet.setName((String) fieldValues.get("name"));
        pet.setAge((Integer) fieldValues.get("age"));
        PetOutDto petOutDto = dtoMap.map(pet);
        assertEquals(fieldValues.get("id"), petOutDto.getId());
        assertEquals(fieldValues.get("name"), petOutDto.getName());
        assertEquals(fieldValues.get("age"), petOutDto.getAge());
        assertNull(petOutDto.getOwnerId());
        assertNull(petOutDto.getOwnerName());
    }

    @Test
    void shouldCorrectlyMapFromIncomingDto() {
        PetInDto incomingDto = new PetInDto();
        incomingDto.setId((Integer) fieldValues.get("id"));
        incomingDto.setName((String) fieldValues.get("name"));
        incomingDto.setAge((Integer) fieldValues.get("age"));
        incomingDto.setOwnerId((Integer) fieldValues.get("ownerId"));
        incomingDto.setOwnerName((String) fieldValues.get("ownerName"));

        Pet pet = dtoMap.map(incomingDto);
        assertEquals(fieldValues.get("id"), pet.getId());
        assertEquals(fieldValues.get("name"), pet.getName());
        assertEquals(fieldValues.get("age"), pet.getAge());
        assertEquals(fieldValues.get("ownerId"), pet.getOwner().getId());
        assertEquals(fieldValues.get("ownerName"), pet.getOwner().getName());
    }

    @Test
    void shouldCorrectlyMapFromIncompleateIncomingDto() {
        PetInDto incomingDto = new PetInDto();
        Pet petFromDto = dtoMap.map(incomingDto);
        assertNull(petFromDto.getId());
        assertNull(petFromDto.getName());
        assertNull(petFromDto.getAge());
        assertNotNull(petFromDto.getOwner());
        assertNull(petFromDto.getOwner().getId());
        assertNull(petFromDto.getOwner().getName());
    }
}
