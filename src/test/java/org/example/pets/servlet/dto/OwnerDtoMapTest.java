package org.example.pets.servlet.dto;

import org.example.pets.model.Owner;
import org.example.pets.model.Pet;
import org.example.pets.servlet.mapper.OwnerDtoMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OwnerDtoMapTest {
    private static Map<String, Object> fieldValues;

    private static OwnerDtoMap dtoMap;

    private static final Integer OWNER_ID = 1;
    private static final String OWNER_NAME = "David";
    private static final Integer PET_ID = 5;
    private static final String PET_NAME = "Rikki";
    private static final Integer PET_AGE = 1;

    @BeforeAll
    static void init() {
        fieldValues = new HashMap<>();
        fieldValues.put("id", 1);
        fieldValues.put("name", "David");

        dtoMap = new OwnerDtoMap();
    }

    @Test
    void shouldCorrectlyMapToOutgoingDto() {
        Owner owner = new Owner(OWNER_ID, OWNER_NAME, null);
        Pet pet = new Pet(PET_ID, PET_NAME, PET_AGE, owner);
        owner.setPets(List.of(pet));

        OwnerOutDto ownerOutDto = dtoMap.map(owner);
        assertEquals(OWNER_ID, ownerOutDto.getId());
        assertEquals(OWNER_NAME, ownerOutDto.getName());
        assertEquals(PET_ID, ownerOutDto.getPets().getFirst().getId());
        assertEquals(PET_NAME, ownerOutDto.getPets().getFirst().getName());
        assertEquals(PET_AGE, ownerOutDto.getPets().getFirst().getAge());
    }

    @Test
    void shouldCorrectlyMapToOutgoingDtoWithoutPets() {
        Owner owner = new Owner(OWNER_ID, OWNER_NAME, null);

        OwnerOutDto ownerOutDto = dtoMap.map(owner);
        assertEquals(OWNER_ID, ownerOutDto.getId());
        assertEquals(OWNER_NAME, ownerOutDto.getName());
        assertNull(ownerOutDto.getPets());
    }

    @Test
    void shouldCorrectlyMapFromIncomingDto() {
        OwnerInDto incomingDto = new OwnerInDto();
        incomingDto.setId(OWNER_ID);
        incomingDto.setName(OWNER_NAME);

        Owner owner = dtoMap.map(incomingDto);
        assertEquals(OWNER_ID, owner.getId());
        assertEquals(OWNER_NAME, owner.getName());
    }
}
