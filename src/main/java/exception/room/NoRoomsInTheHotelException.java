package exception.room;

public class NoRoomsInTheHotelException extends RuntimeException {

    public NoRoomsInTheHotelException(String message) {
        super(message);
    }
}