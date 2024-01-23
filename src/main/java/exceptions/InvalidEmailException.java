package exceptions;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException (String mail) {
        super(String.format("The provided email %s not valid. Please try with a different email!", mail));
    }
}
