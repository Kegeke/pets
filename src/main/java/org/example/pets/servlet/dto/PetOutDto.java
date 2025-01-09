package org.example.pets.servlet.dto;

public class PetOutDto {
    private Integer id;
    private String name;
    private Integer age;
    private Integer ownerId;
    private String ownerName;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public PetOutDto setId(Integer id) {
        this.id = id;
        return this;
    }

    public PetOutDto setName(String name) {
        this.name = name;
        return this;
    }

    public PetOutDto setAge(Integer age) {
        this.age = age;
        return this;
    }

    public PetOutDto setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public PetOutDto setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        return this;
    }
}
