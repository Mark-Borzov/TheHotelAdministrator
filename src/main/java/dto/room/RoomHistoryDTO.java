package dto.room;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

// Дто для Ответа Истории Номеров:
@Getter
@Setter
public class RoomHistoryDTO {

    // Сообщение для Пользователя:
    private String message;

    // ID Номера:
    private UUID roomID;

    // Номер Комнаты:
    private int roomNumber;

    // Имя Жителя:
    private String tenantName;

    // ИНН Жителя:
    private String tenantINN;

    // Дата Заселения Жителя в Номер:
    private LocalDate checkInDate;

    // Дата Освобождения Номера Жителем:
    private LocalDate dateOfIssueOfTheRoom;

    public RoomHistoryDTO(String message, String tenantName, String tenantINN,
                          LocalDate checkInDate, LocalDate dateOfIssueOfTheRoom,
                          UUID roomID, int roomNumber) {
        this.message = message;
        this.roomID = roomID;
        this.roomNumber = roomNumber;
        this.tenantName = tenantName;
        this.tenantINN = tenantINN;
        this.checkInDate = checkInDate;
        this.dateOfIssueOfTheRoom = dateOfIssueOfTheRoom;
    }
}