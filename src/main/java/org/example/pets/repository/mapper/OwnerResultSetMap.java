package org.example.pets.repository.mapper;

import org.example.pets.model.Owner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OwnerResultSetMap {
    public List<Owner> map(ResultSet resultSet) throws SQLException {
        List<Owner> owners = new ArrayList<>();

        while (resultSet.next()) {
            Owner owner = new Owner();

            owner.setId(resultSet.getInt("id"));
            owner.setName(resultSet.getString("name"));
            owners.add(owner);
        }

        return owners;
    }
}
