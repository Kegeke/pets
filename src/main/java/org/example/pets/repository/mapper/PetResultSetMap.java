package org.example.pets.repository.mapper;

import org.example.pets.model.Owner;
import org.example.pets.model.Pet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PetResultSetMap {
    public List<Pet> map(ResultSet resultSet) throws SQLException {
        List<Pet> pets = new ArrayList<>();

        while (resultSet.next()) {
            Pet pet = new Pet();
            pet.setId(resultSet.getInt("id"));
            pet.setName(resultSet.getString("name"));
            pet.setAge(resultSet.getInt("age"));

            Owner owner = new Owner();
            owner.setId(resultSet.getInt("owner_id"));
            owner.setName(resultSet.getString("owner_name"));
            pet.setOwner(owner);

            pets.add(pet);
        }

        return pets;
    }
}
