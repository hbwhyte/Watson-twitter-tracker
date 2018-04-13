package twitter.exceptions.custom_exceptions;

public class EmptySearchException extends Exception {

    public EmptySearchException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "Empty Search";
    }
}
