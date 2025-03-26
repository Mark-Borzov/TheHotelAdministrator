package model.tenant;

import static model.IDGenerator.generateID;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.temporal.ChronoUnit;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import java.time.LocalDate;
import model.room.Room;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tenants")
public class Tenant {

    // Имя Жителя:
    @Column(name = "tenant_name")
    private String tenantName;

    // ИНН Жителя:
    @Column(name = "tenant_inn")
    private String tenantINN;

    // Дата Заселения в Номер:
    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    // Дата Освобождения Номера:
    @Column(name = "date_of_issue_of_the_room")
    private LocalDate dateOfIssueOfTheRoom;

    // Номер комнаты:
    @Column(name = "room_number")
    private int roomNumber;

    // ID Жителя:
    @Id
    @Column(name = "tenant_id")
    private UUID tenantID;

    // Ссылка на Номер для Hibernate:
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    // Конструктор без Параметров для Hibernate:
    public Tenant() {
        // ID Жителя:
        this.tenantID = generateID();
    }

    // Метод для Получения Оплаты за Номер:
    public double paymentForRoom(Room room) {
        long numberOfNights = ChronoUnit.DAYS.between(checkInDate, dateOfIssueOfTheRoom);
        return room.getRoomPrice() * numberOfNights;
    }

    @Override
    public String toString() {
        return  "ID Жителя: " + this.getTenantID() +
                "\nИмя Жителя: " + this.getTenantName() +
                "\nИНН Жителя: " + this.getTenantINN() +
                "\nДата Заселения в Номер: " + this.getCheckInDate() +
                "\nДата Освобождения Номера: " + this.getDateOfIssueOfTheRoom() +
                "\nНомер Комнаты: " + this.getRoomNumber();
    }
}