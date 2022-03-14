package errorhandling;

public class PersonNotFoundException {
    private int code;
    private String msg;

    public PersonNotFoundException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
