package org.example.domain.loginandregister;

public class UsernameDuplicateException extends RuntimeException{

    public UsernameDuplicateException(String username) {
        super(String.format("User with this username already exist " + username));
    }
}
