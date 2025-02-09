package br.com.vss.resell_platform.exceptions;

public class InvalidOwnerException extends RuntimeException {

  public InvalidOwnerException() {
    super("This user is not the owner of this Item.");
  }

    public InvalidOwnerException(String message) {
        super(message);
    }
}
