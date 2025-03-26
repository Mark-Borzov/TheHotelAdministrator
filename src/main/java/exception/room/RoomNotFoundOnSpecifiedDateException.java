package exception.room;

public class RoomNotFoundOnSpecifiedDateException extends RuntimeException {

    public RoomNotFoundOnSpecifiedDateException(String message) {
        super(message);
    }
}
