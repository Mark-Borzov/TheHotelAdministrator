package dto.tenant;

import model.tenant.Tenant;
import java.time.LocalDate;
import model.room.Room;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

// Дто для Ответа Пользователя:
@Getter
@Setter
public class TenantResponseDTO {

    // Сообщение для Пользователя:
    private String message;

    // ID Жителя:
    private UUID tenantID;

    // Имя Жителя:
    private String tenantName;

    // Инн Жителя:
    private String tenantINN;

    // Дата Заселения в Номер:
    private LocalDate checkInDate;

    // Дата Освобождения Номера:
    private LocalDate dateOfIssueOfTheRoom;

    // Номер Комнаты:
    private int roomNumber;

    public TenantResponseDTO(String message, Tenant tenant, Room room) {
        this.message = message;
        this.tenantID = tenant.getTenantID();
        this.tenantName = tenant.getTenantName();
        this.tenantINN = tenant.getTenantINN();
        this.checkInDate = tenant.getCheckInDate();
        this.dateOfIssueOfTheRoom = tenant.getDateOfIssueOfTheRoom();
        this.roomNumber = room.getRoomNumber();
    }
}