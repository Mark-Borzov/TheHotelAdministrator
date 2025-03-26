package controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import dto.service.ServiceProvidedResponseDTO;
import dto.service.CreateServiceRequestDTO;
import org.springframework.http.MediaType;
import dto.service.ServiceResponseDTO;
import lombok.extern.slf4j.Slf4j;
import service.ServiceService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/service")
public class ServiceController {

    private final ServiceService serviceService;

    @Autowired
    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // Метод Добавления Услуги для Жителя Гостиницы:
    @PostMapping
    public ResponseEntity<ServiceResponseDTO> createServiceForTenant(
            @RequestBody CreateServiceRequestDTO request) {
        log.info("Запрос на Добавление Услуги: {} для Жителя с ИНН: {}",
                request.getServiceName(), request.getTenantINN());
        ServiceResponseDTO response = serviceService.createServiceForTenant(request);
        log.info("Услуга \"{}\" Успешно Добавлена для Жителя с ИНН \"{}\".", response.getServiceName(),
                response.getTenantINN());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    // Метод для Получения Списка всех Предоставляемых Услуг (Отсортированных по Цене):
    @GetMapping("/services-provided")
    public ResponseEntity<List<ServiceProvidedResponseDTO>> getServicesProvided() {
        log.info("Запрос на Получение Списка всех Предоставляемых Услуг Гостиницы.");
        List<ServiceProvidedResponseDTO> serviceResponseDTOs = serviceService.getServicesProvidedSortedByPrice();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(serviceResponseDTOs);
    }

    // Метод для Получения Списка Подключенных Услуг Жителя (Отсортированные по Передаваемому Параметру):
    @GetMapping("/sorted")
    public ResponseEntity<List<ServiceResponseDTO>> sortingServices(
            @RequestParam("tenantINN") String tenantINN,
            @RequestParam("sortBy") String sortBy) {
        log.info("Запрос на Получение Списка Подключенных Услуг для Жителя с ИНН: {} Отсортированных по Параметру: {}",
                tenantINN, sortBy);
        List<ServiceResponseDTO> sortedServices = serviceService.getSortedServices(tenantINN, sortBy);
        log.info("Услуг Получено: {}", sortedServices.size());
        return ResponseEntity.ok().body(sortedServices);
    }
}