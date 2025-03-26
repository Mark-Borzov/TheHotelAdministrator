package exception.hotel;

public class HotelNotFoundException extends RuntimeException {

    public HotelNotFoundException(String message) {
        super(message);
    }
}