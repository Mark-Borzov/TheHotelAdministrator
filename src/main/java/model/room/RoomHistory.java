package model.room;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "room_history")
public class RoomHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyID;

    @Column(name = "room_id")
    private UUID roomID;

    @Column(name = "room_number")
    private int roomNumber;

    @Column(name = "tenant_name")
    private String tenantName;

    @Column(name = "tenant_inn")
    private String tenantInn;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "date_of_issue_of_the_room")
    private LocalDate dateOfIssueOfTheRoom;

    // Конструктор без Параметров для Hibernate:
    public RoomHistory() {
    }

    @Override
    public String toString() {
        return  "ID Записи: " + this.getHistoryID() +
                "\nID Номера: " + this.getRoomID() +
                "\nНомер Комнаты: " + this.getRoomNumber() +
                "\nИмя Жителя: " + this.getTenantName() +
                "\nИНН Жителя: " + this.getTenantInn() +
                "\nДата Заселения Жителя в Номер: " + this.getCheckInDate() +
                "\nДата Освобождения Номера Жителем: " + this.getDateOfIssueOfTheRoom();
    }
}