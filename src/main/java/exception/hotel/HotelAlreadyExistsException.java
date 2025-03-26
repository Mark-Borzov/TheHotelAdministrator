package exception.hotel;

public class HotelAlreadyExistsException extends RuntimeException {

    public HotelAlreadyExistsException(String message) {
        super(message);
    }
}