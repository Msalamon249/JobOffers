package org.example.domain.loginandregister;


import java.sql.SQLOutput;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryLoginRepository implements LoginRepository {

    Map<String, User> map = new ConcurrentHashMap<>();

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(map.get(username));
    }

    @Override
    public User save(User user) {
        String id = UUID.randomUUID().toString();
        User toSave = new User(id, user.username(), user.password());
        map.put(toSave.username(), toSave);
        return toSave;
    }


}
