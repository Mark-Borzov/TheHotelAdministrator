package controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import dto.tenant.CreateTenantRequestDTO;
import dto.tenant.TenantResponseDTO;
import lombok.extern.slf4j.Slf4j;
import service.TenantService;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/tenant")
public class TenantController {

    private final TenantService tenantService;

    @Autowired
    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    // Метод для Заселения Жителя в Номер:
    @PostMapping
    public ResponseEntity<TenantResponseDTO> settlementOfTenant(
            @RequestBody @Validated CreateTenantRequestDTO request) {
        log.info("Запрос на Заселение Жителя: {}", request);
        TenantResponseDTO response = tenantService.settleTenant(request);
        log.info("Житель Успешно Заселен: {}", response);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // Метод для Выселения Жителя из Номера:
    @DeleteMapping
    public ResponseEntity<TenantResponseDTO> evictionOfTenant(
            @RequestParam("roomNumber") int roomNumber,
            @RequestParam("tenantINN") String tenantINN) {
        log.info("Запрос на Выселение Жителя с ИНН: {} из Номера: {}", tenantINN, roomNumber);
        TenantResponseDTO response = tenantService.evictTenant(roomNumber, tenantINN);
        log.info("Житель с ИНН: {} Успешно Выселен.", response.getTenantINN());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // Метод для Получения Списка Жителей и их Номеров (Отсортированных по Передаваемому Параметру):
    @GetMapping("/sorting")
    public ResponseEntity<List<TenantResponseDTO>> sortingTenants(
            @RequestParam("sortBy") String sortBy) {
        log.info("Запрос на Получение Списка Жителей, Отсортированных по Параметру: {}", sortBy);
        List<TenantResponseDTO> response = tenantService.getSortedTenants(sortBy);
        log.info("Получено Жителей: {}", response.size());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // Метод для Получения Количества Жителей в Гостинице:
    @GetMapping("/total-tenants")
    public ResponseEntity<Long> getTotalTenants() {
        log.info("Запрос на Получение Общего Количества Жителей в Гостинице.");
        long totalTenants = tenantService.getTotalTenants();
        log.info("Общее Количество Жителей: {}", totalTenants);
        return ResponseEntity.ok().body(totalTenants);
    }

    // Метод для Получения Оплаты Жителя за Номер и Услуги:
    @GetMapping("/final-payment")
    public ResponseEntity<Double> getFinalPayment(
            @RequestParam("tenantINN") String tenantINN) {
        log.info("Запрос на Получение Оплаты для Жителя с ИНН: {}", tenantINN);
        double finalPayment = tenantService.calculateFinalPayment(tenantINN);
        log.info("Оплата для Жителя с ИНН: {} Составляет: {}", tenantINN, finalPayment);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(finalPayment);
    }
}