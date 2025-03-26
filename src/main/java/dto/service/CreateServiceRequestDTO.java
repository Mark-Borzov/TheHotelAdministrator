package dto.service;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

// Дто для Запроса на Создание Услуги для Жителя:
@Getter
@Setter
public class CreateServiceRequestDTO {

    // Название Услуги:
    private String serviceName;

    // Дата Добавления Услуги:
    private LocalDate serviceDate;

    // ИНН Жителя:
    private String tenantINN;

    // Номер Комнаты Жителя:
    private int roomNumber;
}