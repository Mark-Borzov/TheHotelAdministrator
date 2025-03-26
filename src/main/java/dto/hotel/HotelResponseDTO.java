package dto.hotel;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelResponseDTO {

    // Сообщение для Пользователя:
    private String message;

    // Название Гостиницы:
    private String hotelName;

    // ID Гостиницы:
    private UUID hotelID;

    public HotelResponseDTO(String message, String hotelName, UUID hotelID) {
        this.message = message;
        this.hotelName = hotelName;
        this.hotelID = hotelID;
    }
}