package exception.room;

public class RoomHistoryNotFoundException extends RuntimeException {

    public RoomHistoryNotFoundException(String message) {
        super(message);
    }
}