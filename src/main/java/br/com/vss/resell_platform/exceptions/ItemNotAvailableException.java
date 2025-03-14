package br.com.vss.resell_platform.exceptions;

public class ItemNotAvailableException extends RuntimeException {

  public ItemNotAvailableException() {
    super("Item is currently not available.");
  }

    public ItemNotAvailableException(String message) {
        super(message);
    }
}
