package exception.room;

public class RoomStatusCannotBeChangedException extends RuntimeException {

    public RoomStatusCannotBeChangedException(String message) {
        super(message);
    }
}