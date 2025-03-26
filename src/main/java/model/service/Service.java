package model.service;

import static model.IDGenerator.generateID;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.text.DecimalFormat;
import jakarta.persistence.Id;
import java.time.LocalDate;
import model.tenant.Tenant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "services")
public class Service {

    // Название Услуги:
    @Column(name = "service_name")
    private String serviceName;

    // Цена Услуги:
    @Column(name = "service_price")
    private double servicePrice;

    // Дата Добавления Услуги:
    @Column(name = "service_date")
    private LocalDate serviceDate;

    // ID Услуги:
    @Id
    @Column(name = "service_id")
    private UUID serviceID;

    // Ссылка на Жителя для Hibernate:
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    // Конструктор без Параметров для Hibernate:
    public Service() {
        // ID Услуги:
        this.serviceID = generateID();
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return  "ID Услуги: " + this.getServiceID() +
                "\nУслуга: " + this.getServiceName() +
                "\nЦена Услуги: " + decimalFormat.format(this.getServicePrice()) + " руб." +
                "\nДата добавления Услуги: " + this.getServiceDate();
    }
}