package dto.service;

import model.service.ServicesProvided;
import lombok.Getter;
import lombok.Setter;

// Дто для Предоставляемых Услуг:
@Getter
@Setter
public class ServiceProvidedResponseDTO {

    // Сообщение для Пользователя;
    private String message;

    // Название Предоставляемой Услуги:
    private String serviceProvidedName;

    // Цена Предоставляемой Услуги:
    private double serviceProvidedPrice;

    public ServiceProvidedResponseDTO(String message, ServicesProvided serviceProvided) {
        this.message = message;
        this.serviceProvidedName = serviceProvided.getServiceServicesProvidedName();
        this.serviceProvidedPrice = serviceProvided.getServiceServicesProvidedPrice();
    }
}