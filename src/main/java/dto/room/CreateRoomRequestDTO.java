package dto.room;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import model.room.RoomType;
import lombok.Getter;
import lombok.Setter;

// Дто для Запроса на Создание Номера:
@Getter
@Setter
public class CreateRoomRequestDTO {

    // Сообщения об Ошибках:
    private static final String MAX_CAPACITY_MESSAGE = "Максимальная Вместимость Номера Должна быть от 1 до 5";
    private static final String COUNT_OF_STARS_MESSAGE = "Количество Звезд Номера Должно быть от 1 до 5";

    // Тип Номера:
    private RoomType roomType;

    // Номер Размещения:
    private int roomNumber;

    // Цена Номера:
    private double roomPrice;

    // Максимальная Вместимость Номера:
    @Min(value = 1, message = MAX_CAPACITY_MESSAGE)
    @Max(value = 5, message = MAX_CAPACITY_MESSAGE)
    private int maxCapacity;

    // Количество Звезд Номера:
    @Min(value = 1, message = COUNT_OF_STARS_MESSAGE)
    @Max(value = 5, message = COUNT_OF_STARS_MESSAGE)
    private int countOfStars;
}