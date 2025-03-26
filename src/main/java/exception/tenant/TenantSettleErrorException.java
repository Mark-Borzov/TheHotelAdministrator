package exception.tenant;

public class TenantSettleErrorException extends RuntimeException {

    public TenantSettleErrorException(String message) {
        super(message);
    }
}