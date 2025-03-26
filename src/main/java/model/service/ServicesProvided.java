package model.service;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "services_provided")
public class ServicesProvided {

    // Название Предоставляемой Услуги:
    @Id
    @Column(name = "service_name")
    private String serviceServicesProvidedName;

    // Цена Предоставляемой Услуги:
    @Column(name = "service_price")
    private double serviceServicesProvidedPrice;

    // Конструктор без Параметров для Hibernate:
    public ServicesProvided() {
    }

    @Override
    public String toString() {
        return "Предоставляемая Услуга: " + this.getServiceServicesProvidedName() +
                " - " + this.getServiceServicesProvidedPrice() + " руб.";
    }
}