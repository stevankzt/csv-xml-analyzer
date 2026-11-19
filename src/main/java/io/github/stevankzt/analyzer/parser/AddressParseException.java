package io.github.stevankzt.analyzer.parser;

public class AddressParseException extends Exception {

    private static final long serialVersionUID = 1L;

    public AddressParseException(String message) {
        super(message);
    }

    public AddressParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
