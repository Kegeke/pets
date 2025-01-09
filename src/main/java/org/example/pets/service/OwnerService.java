package org.example.pets.service;

import org.example.pets.model.Owner;
import org.example.pets.repository.Repository;
import org.example.pets.repository.RepositoryFactory;
import org.example.pets.servlet.dto.OwnerInDto;
import org.example.pets.servlet.dto.OwnerOutDto;
import org.example.pets.servlet.mapper.OwnerDtoMap;

import java.util.List;
import java.util.Optional;

public class OwnerService {
    private Repository<Owner, Integer> ownerRepository;
    private final OwnerDtoMap ownerDtoMap;

    public OwnerService() {
        ownerRepository = RepositoryFactory.getRepository(Owner.class, Integer.class);
        ownerDtoMap = new OwnerDtoMap();
    }

    public Repository<Owner, Integer> getOwnerRepository() {
        return ownerRepository;
    }

    public void setOwnerRepository(Repository<Owner, Integer> ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    public OwnerOutDto createOwner(OwnerInDto owner) {
        ownerDataValidation(owner);
        return ownerDtoMap.map(ownerRepository.save(ownerDtoMap.map(owner)));
    }

    public List<OwnerOutDto> getOwners() {
        return ownerRepository.findAll().stream().map(this::mapToOutDto).toList();
    }

    public Optional<OwnerOutDto> getOwnerById(int id) {
        Optional<Owner> optionalOwner = ownerRepository.findById(id);

        return optionalOwner.isEmpty() ? Optional.empty()
                : Optional.of(mapToOutDto(optionalOwner.get()));
    }

    public OwnerOutDto updateOwner(OwnerInDto owner) {
        if (owner.getId() == null) {
            throw new RuntimeException("Должен быть id хозяина");
        }

        ownerDataValidation(owner);
        Owner uptadeOwner = ownerRepository.update(mapFromInDto(owner)).orElseThrow();

        return mapToOutDto(uptadeOwner);
    }

    public boolean deleteOwner(int id) {
        return ownerRepository.deleteById(id);
    }

    private void ownerDataValidation(OwnerInDto owner) {
        if (owner.getName() == null) {
            throw new RuntimeException("Должно быть имя хозяина");
        }
    }

    private OwnerOutDto mapToOutDto(Owner owner) {
        return ownerDtoMap.map(owner);
    }

    private Owner mapFromInDto(OwnerInDto owner) {
        return ownerDtoMap.map(owner);
    }
}
