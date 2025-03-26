package exception.tenant;

public class NoTenantsInTheHotelException extends RuntimeException {

    public NoTenantsInTheHotelException(String message) {
        super(message);
    }
}