package org.example.pets.repository;

import org.example.pets.db.ConnectionManager;
import org.example.pets.model.Owner;
import org.example.pets.model.Pet;

import java.util.HashMap;
import java.util.Map;

public class RepositoryFactory {
    private static final Map<Class<?>, Repository<?, ?>> map;

    static {
        map = new HashMap<>();
        PetRepository petRepository = new PetRepository();
        OwnerRepository ownerRepository = new OwnerRepository();
        petRepository.setOwnerRepository(ownerRepository);
        map.put(Pet.class, petRepository);
        map.put(Owner.class, ownerRepository);

        try {
            ConnectionManager connectionManager = new ConnectionManager();
            setConnectionManager(connectionManager);
        } catch (Exception e){
        }
    }

    private RepositoryFactory(){
    }

    @SuppressWarnings("unchecked")
    public static <T, K> Repository<T, K> getRepository(Class<T> valueClass, Class<K> keyClass) {
        if (keyClass != Integer.class)
            throw new IllegalArgumentException();
        return (Repository<T, K>) map.get(valueClass);
    }

    public static void setConnectionManager(ConnectionManager connectionManager) {
        map.values().forEach(v -> v.setConnectionManager(connectionManager));
        map.values().forEach(Repository::initDB);
    }
}
