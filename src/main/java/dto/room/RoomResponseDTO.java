package dto.room;

import model.room.RoomStatus;
import model.room.RoomType;
import model.room.Room;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

// Дто для Ответа Номера:
@Getter
@Setter
public class RoomResponseDTO {

    // Сообщение для Пользователя:
    private String message;

    // ID Номера:
    private UUID roomID;

    // Номер Размещения:
    private int roomNumber;

    // Тип Номера:
    private RoomType roomType;

    // Статус Номера
    private RoomStatus roomStatus;

    // Цена Номера:
    private double roomPrice;

    // Максимальная Вместимость Номера:
    private int maxCapacity;

    // Количество Звезд Номера:
    private int countOfStars;

    // Параметр для Установки Количества записей о Жителях в Истории Номера:
    private int tenantHistorySize;

    public RoomResponseDTO(String message, Room room) {
        this.message = message;
        this.roomID = room.getRoomID();
        this.roomNumber = room.getRoomNumber();
        this.roomType = room.getRoomType();
        this.roomStatus = room.getRoomStatus();
        this.roomPrice = room.getRoomPrice();
        this.maxCapacity = room.getMaxCapacity();
        this.countOfStars = room.getCountOfStars();
        this.tenantHistorySize = room.getTenantHistorySize();
    }
}