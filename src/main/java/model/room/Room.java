package model.room;

import static model.IDGenerator.generateID;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.text.DecimalFormat;
import jakarta.persistence.Id;
import model.hotel.Hotel;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rooms")
public class Room {

    // Параметр для Установки Количества записей о Жителях в Истории Номера:
    @Column(name = "tenant_history_size")
    private int tenantHistorySize;

    // Тип Номера:
    @Column(name = "room_type")
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    // Номер Размещения:
    @Column(name = "room_number")
    private int roomNumber;

    // Статус Номера:
    @Column(name = "room_status")
    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    // Цена Номера:
    @Column(name = "room_price")
    private double roomPrice;

    // Максимальная Вместимость Номера:
    @Column(name = "max_capacity")
    private int maxCapacity;

    // Количество Звезд Номера:
    @Column(name = "count_of_stars")
    private int countOfStars;

    // ID Номера:
    @Id
    @Column(name = "room_id")
    private UUID roomID;

    // Ссылка на Гостиницу для Hibernate:
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    // Конструктор без Параметров для Hibernate:
    public Room() {
        // Идентификатор Номера:
        this.roomID = generateID();
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return  "ID Номера: " + this.getRoomID() +
                "\nНомер Размещения: " + this.getRoomNumber() +
                "\nТип Номера: " + this.getRoomType() +
                "\nСтатус Номера: " + this.getRoomStatus() +
                "\nЦена Номера: " + decimalFormat.format(this.getRoomPrice()) + " руб." +
                "\nМаксимальная Вместимость Номера: " + this.getMaxCapacity() +
                "\nКоличество Звезд Номера: " + this.getCountOfStars();
    }
}