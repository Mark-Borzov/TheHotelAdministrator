package service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import exception.service.ServiceAdditionDateException;
import exception.tenant.NoTenantsInTheHotelException;
import exception.service.ServiceNotFoundException;
import exception.tenant.TenantNotFoundException;
import org.springframework.stereotype.Service;
import dto.service.ServiceProvidedResponseDTO;
import dto.service.CreateServiceRequestDTO;
import model.service.ServicesProvided;
import dto.service.ServiceResponseDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import model.tenant.Tenant;
import java.util.List;
import dao.ServiceDAO;
import dao.TenantDAO;

@Service
public class ServiceService {

    private final RoomService roomService;
    private final TenantDAO tenantDAO;
    private final ServiceDAO serviceDAO;

    @Autowired
    public ServiceService(TenantDAO tenantDAO, ServiceDAO serviceDAO, RoomService roomService) {
        this.tenantDAO = tenantDAO;
        this.serviceDAO = serviceDAO;
        this.roomService = roomService;
    }

    // Метод для Добавления Услуги для Жителя Гостиницы через "ServiceService":
    @Transactional
    public ServiceResponseDTO createServiceForTenant(CreateServiceRequestDTO request) {
        roomService.checkHotelExists();
        roomService.checkRoomsExist();
        if (tenantDAO.availabilityOfTenant()) {
            throw new NoTenantsInTheHotelException("Ошибка: В Гостинице нет Жителей.");
        }
        Tenant tenant = tenantDAO.findTenantByInnAndRoomNumber(request.getTenantINN(), request.getRoomNumber());
        if (tenant == null) {
            throw new TenantNotFoundException("Ошибка: Житель с ИНН " + request.getTenantINN() + " не Найден.");
        }
        LocalDate checkInDate = tenant.getCheckInDate();
        LocalDate checkOutDate = tenant.getDateOfIssueOfTheRoom();
        if (request.getServiceDate().isBefore(checkInDate) || request.getServiceDate().isAfter(checkOutDate)) {
            throw new ServiceAdditionDateException(
                    "Ошибка: Дата Услуги Должна быть не Раньше Даты Заселения и не Позже Даты Выселения.");
        }
        ServicesProvided servicesProvided = serviceDAO.getServiceByName(request.getServiceName());
        if (servicesProvided == null) {
            throw new ServiceNotFoundException("Ошибка: Услуга с Именем " + request.getServiceName() + " не Найдена.");
        }
        model.service.Service service = new model.service.Service();
        service.setServiceName(servicesProvided.getServiceServicesProvidedName());
        service.setServicePrice(servicesProvided.getServiceServicesProvidedPrice());
        service.setServiceDate(request.getServiceDate());
        service.setTenant(tenant);
        serviceDAO.addServiceForTenant(service);
        return new ServiceResponseDTO("Услуга Успешно Добавлена:", service, tenant);
    }

    // Метод для Получения Списка Подключенных Услуг Жителя (Отсортированные по Параметру) через "ServiceService":
    @Transactional(readOnly = true)
    public List<ServiceResponseDTO> getSortedServices(String tenantINN, String sortBy) {
        roomService.checkHotelExists();
        roomService.checkRoomsExist();
        Tenant tenant = tenantDAO.findTenantByInn(tenantINN);
        if (tenant == null) {
            throw new TenantNotFoundException("Ошибка: Житель с ИНН: " + tenantINN + " не Найден.");
        }
        return this.getSortedServicesForTenant(tenant, sortBy);
    }

    // Метод для Получения Списка всех Предоставляемых Услуг (Отсортированных по Цене) через "ServiceService":
    @Transactional(readOnly = true)
    public List<ServiceProvidedResponseDTO> getServicesProvidedSortedByPrice() {
        roomService.checkHotelExists();
        roomService.checkRoomsExist();
        List<ServicesProvided> services = serviceDAO.getServicesProvidedSortedByPrice();
        List<ServiceProvidedResponseDTO> serviceResponseDTOs = new ArrayList<>();
        for (ServicesProvided service : services) {
            ServiceProvidedResponseDTO dto = new ServiceProvidedResponseDTO("Предоставляемая Услуга:", service);
            serviceResponseDTOs.add(dto);
        }
        return serviceResponseDTOs;
    }

    // Вспомогательный Метод для Получения Списка Подключенных Услуг Жителя (Отсортированные по Параметру):
    private List<ServiceResponseDTO> getSortedServicesForTenant(Tenant tenant, String sortBy) {
        if (tenantDAO.availabilityOfTenant()) {
            throw new NoTenantsInTheHotelException("Ошибка: В Гостинице нет Жителей.");
        }
        List<model.service.Service> services = serviceDAO.getSortedServicesForTenant(tenant.getTenantID(), sortBy);
        if (services.isEmpty()) {
            throw new ServiceNotFoundException("Ошибка: У Данного Жителя нет Подключенных Услуг");
        }
        List<ServiceResponseDTO> serviceResponseDTOs = new ArrayList<>();
        for (model.service.Service service : services) {
            ServiceResponseDTO dto = new ServiceResponseDTO("Подключенная Услуга:", service, tenant);
            serviceResponseDTOs.add(dto);
        }
        return serviceResponseDTOs;
    }
}