package dto.service;

import model.service.Service;
import model.tenant.Tenant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

// Дто для Ответа Предоставляемой Услуги:
@Getter
@Setter
public class ServiceResponseDTO {

    // Сообщение Пользователю:
    private String message;

    // ID Услуги:
    private UUID serviceID;

    // Название Услуги:
    private String serviceName;

    // Цена Услуги:
    private double servicePrice;

    // Дата Добавления Услуги:
    private LocalDate serviceDate;

    // ИНН Жителя:
    private String tenantINN;

    // Имя Жителя:
    private String tenantName;

    // Номер Комнаты:
    private int roomNumber;

    public ServiceResponseDTO(String message, Service service, Tenant tenant) {
        this.message = message;
        this.serviceID = service.getServiceID();
        this.serviceName = service.getServiceName();
        this.servicePrice = service.getServicePrice();
        this.serviceDate = service.getServiceDate();
        this.tenantINN = tenant.getTenantINN();
        this.tenantName = tenant.getTenantName();
        this.roomNumber = tenant.getRoomNumber();
    }
}