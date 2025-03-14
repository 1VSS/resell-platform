package br.com.vss.resell_platform.exceptions;

public class ItemNotAvailableException extends RuntimeException {

    public ItemNotAvailableException() {
        super("This item is not available for purchase");
    }

    public ItemNotAvailableException(String message) {
        super(message);
    }
}
