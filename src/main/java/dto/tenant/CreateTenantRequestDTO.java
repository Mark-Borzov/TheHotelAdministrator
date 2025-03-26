package dto.tenant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

// Дто для Запроса на Добавление Жителя в Номер:
@Getter
@Setter
public class CreateTenantRequestDTO {

    // Сообщения об Ошибках:
    private static final String TENANT_NAME_MESSAGE = "Имя Жителя не Может быть Пустым или Состоять только из Пробелов.";
    private static final String TENANT_NAME_SIZE_MESSAGE = "Имя Жителя Должно быть от 2 до 20 Символов.";
    private static final String TENANT_INN_MESSAGE = "ИНН Жителя не Может быть Пустым или Состоять только из Пробелов.";
    private static final String TENANT_INN_SIZE_MESSAGE = "ИНН Жителя Должен быть от 10 до 12 Символов.";
    private static final String TENANT_INN_PATTERN_MESSAGE = "ИНН Жителя Должен Содержать только Цифры.";

    // Имя Жителя:
    @NotBlank(message = TENANT_NAME_MESSAGE)
    @Size(min = 2, max = 20, message = TENANT_NAME_SIZE_MESSAGE)
    private String tenantName;

    // ИНН Жителя:
    @NotBlank(message = TENANT_INN_MESSAGE)
    @Size(min = 10, max = 12, message = TENANT_INN_SIZE_MESSAGE)
    @Pattern(regexp = "^[0-9]+$", message = TENANT_INN_PATTERN_MESSAGE)
    private String tenantINN;

    // Номер Комнаты:
    private int roomNumber;

    // Дата Заселения в Номер:
    private LocalDate checkInDate;

    // Дата Освобождения Номера:
    private LocalDate dateOfIssueOfTheRoom;
}