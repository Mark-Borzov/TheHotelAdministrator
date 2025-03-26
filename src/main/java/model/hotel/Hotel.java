package model.hotel;

import jakarta.validation.constraints.NotBlank;
import static model.IDGenerator.generateID;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hotel")
public class Hotel {

    // Название Гостиницы:
    @Column(name = "hotel_name")
    @NotBlank(message = "Название Гостиницы не Может Быть Пустым или Состоять только из Пробелов.")
    @Size(min = 3, max = 50, message = "Имя Гостиницы Должно Быть от 3 до 50 Символов.")
    private String hotelName;

    // ID Гостиницы:
    @Id
    @Column(name = "hotel_id")
    private UUID hotelID;

    // Конструктор без Параметров для Hibernate:
    public Hotel() {
        // ID Гостиницы:
        this.hotelID = generateID();
    }

    @Override
    public String toString() {
        return  "ID Гостиницы: " + getHotelID() + "\nНазвание Гостиницы: " + this.getHotelName();
    }
}