package org.example.pets.servlet.mapper;

import org.example.pets.model.Owner;
import org.example.pets.model.Pet;
import org.example.pets.servlet.dto.OwnerInDto;
import org.example.pets.servlet.dto.OwnerOutDto;
import org.example.pets.servlet.dto.PetOutDto;

import java.util.ArrayList;
import java.util.List;

public class OwnerDtoMap {
    PetDtoMap petDtoMap = new PetDtoMap();

    public Owner map(OwnerInDto ownerInDto) {
        Owner owner = new Owner();
        owner.setId(ownerInDto.getId());
        owner.setName(ownerInDto.getName());

        return owner;
    }

    public OwnerOutDto map(Owner owner) {
        OwnerOutDto ownerOutDto = new OwnerOutDto();
        ownerOutDto.setId(owner.getId());
        ownerOutDto.setName(owner.getName());

        if (owner.getPets() == null) {
            return ownerOutDto;
        }

        List<PetOutDto> pets = new ArrayList<>();
        for (Pet pet : owner.getPets()) {
            pets.add(petDtoMap.map(pet));
        }

        ownerOutDto.setPets(pets);

        return ownerOutDto;
    }

}
