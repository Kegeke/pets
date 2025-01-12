package org.example.pets.service;

import org.example.pets.model.Owner;
import org.example.pets.repository.OwnerRepository;
import org.example.pets.servlet.dto.OwnerInDto;
import org.example.pets.servlet.dto.OwnerOutDto;
import org.example.pets.servlet.mapper.OwnerDtoMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OwnerServiceTest {
    private OwnerService ownerService;
    private OwnerRepository ownerRepository;
    private OwnerDtoMap map;

    @BeforeEach
    void init() {
        ownerRepository = Mockito.mock(OwnerRepository.class);

        ownerService = new OwnerService();
        ownerService.setOwnerRepository(ownerRepository);

        map = new OwnerDtoMap();
    }

    @Test
    void shouldCorrectlyCreateOwnerWithName() {
        OwnerInDto incomingDto = new OwnerInDto(1, "David");
        Mockito.doReturn(new Owner(1, "David", null)).when(ownerRepository)
                .save(Mockito.any(Owner.class));

        OwnerOutDto outgoingDto = ownerService.createOwner(incomingDto);
        assertEquals(incomingDto.getName(), outgoingDto.getName());
    }

    @Test
    void shouldCorrectlyFindAllOwners() {
        List<Owner> ownersFromMockRepository = List.of(
                new Owner(1, "David", null), new Owner(2, "Vasily", null));
        Mockito.when(ownerRepository.findAll()).thenReturn(ownersFromMockRepository);
        List<OwnerOutDto> owners = ownerService.getOwners();
        assertEquals(2, owners.size());
    }

    @Test
    void shouldReturnOptionalEmptyIfRepositoryReturnOptionalEmpty() {
        Mockito.doReturn(Optional.empty()).when(ownerRepository).findById(Mockito.anyInt());
        Optional<OwnerOutDto> owner = ownerService.getOwnerById(0);
        assertTrue(owner.isEmpty());
    }

    @Test
    void shouldReturnOwnerById() {
        Mockito.doReturn(Optional.of(new Owner(1, "David", null)))
                .when(ownerRepository).findById(Mockito.anyInt());
        Optional<OwnerOutDto> director = ownerService.getOwnerById(1);
        assertTrue(director.isPresent());
        assertEquals("David", director.get().getName());
    }

    @Test
    void shouldThrowExceptionIfUpdateWithoutId() {
        OwnerInDto ownerInDto = new OwnerInDto(null, "David");
        assertThrows(RuntimeException.class,
                () -> ownerService.updateOwner(ownerInDto));
    }

    @Test
    void shouldThrowExceptionIfUpdateWithoutName() {
        OwnerInDto ownerInDto = new OwnerInDto(1, null);
        assertThrows(RuntimeException.class,
                () -> ownerService.updateOwner(ownerInDto));
    }

    @Test
    void shouldCorrectlyUpdate() {
        OwnerInDto ownerInDto = new OwnerInDto(1, "David");
        Mockito.doReturn(Optional.of(map.map(ownerInDto))).when(ownerRepository)
                .update(Mockito.any(Owner.class));
        OwnerOutDto owner = ownerService.updateOwner(ownerInDto);
        assertEquals(ownerInDto.getName(), owner.getName());
    }

    @Test
    void shouldReturnTheSameBooleanValueAsRepositoryWhenDelete() {
        Mockito.doReturn(true).when(ownerRepository).deleteById(Mockito.anyInt());
        assertTrue(ownerService.deleteOwner(1));
        Mockito.doReturn(false).when(ownerRepository).deleteById(Mockito.anyInt());
        assertFalse(ownerService.deleteOwner(1));
    }

    @Test
    void shouldReturnOwnerRepository() {
        assertEquals(ownerRepository, ownerService.getOwnerRepository());
    }

}
