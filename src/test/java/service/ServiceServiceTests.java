package service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import dto.service.ServiceProvidedResponseDTO;
import dto.service.CreateServiceRequestDTO;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import model.service.ServicesProvided;
import dto.service.ServiceResponseDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import model.service.Service;
import model.tenant.Tenant;
import java.time.LocalDate;
import java.util.ArrayList;
import org.mockito.Mock;
import java.util.List;
import java.util.UUID;
import dao.ServiceDAO;
import dao.TenantDAO;

@ExtendWith(MockitoExtension.class)
public class ServiceServiceTests {

    @Mock
    private TenantDAO tenantDAO;

    @Mock
    private ServiceDAO serviceDAO;

    @Mock
    @SuppressWarnings("unused")
    private RoomService roomService;

    @InjectMocks
    private ServiceService serviceService;

    // Тест: "Метод для Добавления Услуги для Жителя Гостиницы":
    // Результат: "Успешно":
    @Test
    public void createServiceForTenantTest() {
        CreateServiceRequestDTO request = new CreateServiceRequestDTO();
        request.setServiceName("Завтрак");
        request.setServiceDate(LocalDate.now());
        request.setTenantINN("1234567890");
        request.setRoomNumber(101);
        Tenant tenant = new Tenant();
        tenant.setCheckInDate(LocalDate.now().minusDays(1));
        tenant.setDateOfIssueOfTheRoom(LocalDate.now().plusDays(1));
        ServicesProvided servicesProvided = new ServicesProvided();
        servicesProvided.setServiceServicesProvidedName("Завтрак");
        servicesProvided.setServiceServicesProvidedPrice(100);
        when(tenantDAO.availabilityOfTenant()).thenReturn(false);
        when(tenantDAO.findTenantByInnAndRoomNumber(request.getTenantINN(), request.getRoomNumber())).thenReturn(tenant);
        when(serviceDAO.getServiceByName(request.getServiceName())).thenReturn(servicesProvided);
        ServiceResponseDTO response = serviceService.createServiceForTenant(request);
        assertNotNull(response);
        assertEquals("Услуга Успешно Добавлена:", response.getMessage());
        assertEquals("Завтрак", response.getServiceName());
        assertEquals(100, response.getServicePrice(), 0);
        verify(serviceDAO).addServiceForTenant(any(Service.class));
    }

    // Тест: "Метод для Получения Списка Подключенных Услуг Жителя (Отсортированные по Цене)":
    // Результат: "Успешно":
    @Test
    public void getSortedServicesByPriceTest() {
        String tenantINN = "1234567890";
        String sortBy = "Цена";
        Tenant tenant = new Tenant();
        tenant.setTenantID(UUID.randomUUID());
        List<Service> services = new ArrayList<>();
        Service service1 = new Service();
        service1.setServicePrice(200);
        service1.setServiceDate(LocalDate.now().minusDays(1));
        services.add(service1);
        Service service2 = new Service();
        service2.setServicePrice(100);
        service2.setServiceDate(LocalDate.now());
        services.add(service2);
        when(tenantDAO.findTenantByInn(tenantINN)).thenReturn(tenant);
        when(serviceDAO.getSortedServicesForTenant(tenant.getTenantID(), sortBy)).thenReturn(services);
        List<ServiceResponseDTO> response = serviceService.getSortedServices(tenantINN, sortBy);
        assertEquals(2, response.size());
        assertEquals(200, response.get(0).getServicePrice(), 0);
        assertEquals(100, response.get(1).getServicePrice(), 0);
    }

    // Тест: "Метод для Получения Списка Подключенных Услуг Жителя (Отсортированные по Дате Добавления)":
    // Результат: "Успешно":
    @Test
    public void getSortedServicesByDateTest() {
        String tenantINN = "1234567890";
        String sortBy = "Дата";
        Tenant tenant = new Tenant();
        tenant.setTenantID(UUID.randomUUID());
        List<Service> services = new ArrayList<>();
        Service service1 = new Service();
        service1.setServicePrice(200);
        service1.setServiceDate(LocalDate.now().minusDays(1));
        services.add(service1);
        Service service2 = new Service();
        service2.setServicePrice(100);
        service2.setServiceDate(LocalDate.now());
        services.add(service2);
        when(tenantDAO.findTenantByInn(tenantINN)).thenReturn(tenant);
        when(serviceDAO.getSortedServicesForTenant(tenant.getTenantID(), sortBy)).thenReturn(services);
        List<ServiceResponseDTO> response = serviceService.getSortedServices(tenantINN, sortBy);
        assertEquals(2, response.size());
        assertEquals(service1.getServiceDate(), response.get(0).getServiceDate());
        assertEquals(service2.getServiceDate(), response.get(1).getServiceDate());
    }

    // Тест: "Метод для Получения Списка всех Предоставляемых Услуг (Отсортированных по Цене)":
    // Результат: "Успешно":
    @Test
    public void getServicesProvidedSortedByPriceTest() {
        List<ServicesProvided> services = new ArrayList<>();
        ServicesProvided service1 = new ServicesProvided();
        service1.setServiceServicesProvidedName("Обед");
        service1.setServiceServicesProvidedPrice(200);
        services.add(service1);
        ServicesProvided service2 = new ServicesProvided();
        service2.setServiceServicesProvidedName("Завтрак");
        service2.setServiceServicesProvidedPrice(100);
        services.add(service2);
        when(serviceDAO.getServicesProvidedSortedByPrice()).thenReturn(services);
        List<ServiceProvidedResponseDTO> response = serviceService.getServicesProvidedSortedByPrice();
        assertEquals(2, response.size());
        assertEquals("Обед", response.get(0).getServiceProvidedName());
        assertEquals(200, response.get(0).getServiceProvidedPrice(), 0);
        assertEquals("Завтрак", response.get(1).getServiceProvidedName());
        assertEquals(100, response.get(1).getServiceProvidedPrice(), 0);
    }
}