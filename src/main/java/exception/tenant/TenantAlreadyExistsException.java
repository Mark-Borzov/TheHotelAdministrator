package exception.tenant;

public class TenantAlreadyExistsException extends RuntimeException {

    public TenantAlreadyExistsException(String message) {
        super(message);
    }
}