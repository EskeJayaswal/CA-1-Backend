package errorhandling;

public class EntityAlreadyExistsException extends Exception{
    public EntityAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
