package org.example.pets.servlet.dto;

public class OwnerInDto {
    private Integer id;
    private String name;

    public OwnerInDto() {
    }

    public OwnerInDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public OwnerInDto setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public OwnerInDto setName(String name) {
        this.name = name;
        return this;
    }
}
