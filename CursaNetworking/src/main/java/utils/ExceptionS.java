package utils;

public class ExceptionS extends Exception{
    public ExceptionS() {

    }

    public ExceptionS(String message) {
        super(message);
    }

    public ExceptionS(String message, Throwable cause) {
        super(message, cause);
    }
}