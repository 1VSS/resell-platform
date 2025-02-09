package br.com.vss.resell_platform.exceptions;

public class EmailAlreadyTakenException extends RuntimeException {
    public EmailAlreadyTakenException() {
      super("Email already taken.");
    }
    public EmailAlreadyTakenException(String message) {
        super(message);
    }
}
