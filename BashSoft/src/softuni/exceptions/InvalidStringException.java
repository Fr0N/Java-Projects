package softuni.exceptions;

public class InvalidStringException extends RuntimeException{

    private static final String NULL_OR_EMPTY_VALUE = "The value of the variable CANNOT be null or empty!";

    public InvalidStringException() {
    }

    public InvalidStringException(String message) {
        super(message);
    }
}
