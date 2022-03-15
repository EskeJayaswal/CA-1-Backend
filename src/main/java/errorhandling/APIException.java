package errorhandling;

public class APIException extends Exception{
    public APIException(String errorMessage) {
        super(errorMessage);
    }
}
