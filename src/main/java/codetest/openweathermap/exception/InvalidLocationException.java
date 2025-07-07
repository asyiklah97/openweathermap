package codetest.openweathermap.exception;

public class InvalidLocationException extends Exception {

    private static final long serialVersionUID = -3497154677458274353L;

    public InvalidLocationException() {
        super();
    }

    public InvalidLocationException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidLocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidLocationException(String message) {
        super(message);
    }

    public InvalidLocationException(Throwable cause) {
        super(cause);
    }
}
