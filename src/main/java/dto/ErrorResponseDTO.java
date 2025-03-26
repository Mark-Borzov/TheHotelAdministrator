package dto;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponseDTO {

    // Сообщение Исключения:
    private String message;

    // Статус Ошибки:
    private int status;

    // Время Возникновения Ошибки:
    private String timestamp;

    public ErrorResponseDTO(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = formatTimestamp(LocalDateTime.now());
    }

    // Метод для форматирования времени
    private String formatTimestamp(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("Дата: yyyy-MM-dd. Время: HH:mm:ss");
        return dateTime.format(formatter);
    }
}