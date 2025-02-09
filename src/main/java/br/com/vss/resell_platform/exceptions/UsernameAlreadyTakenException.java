package br.com.vss.resell_platform.exceptions;

public class UsernameAlreadyTakenException extends RuntimeException{

    public UsernameAlreadyTakenException() {
        super("Username already taken");
    }

    public UsernameAlreadyTakenException(String message) {
        super(message);
    }
}
