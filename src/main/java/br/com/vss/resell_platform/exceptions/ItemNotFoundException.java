package br.com.vss.resell_platform.exceptions;

public class ItemNotFoundException extends RuntimeException {

  public ItemNotFoundException() {
    super("Item with given ID was not found.");
  }

    public ItemNotFoundException(String message) {
        super(message);
    }
}
