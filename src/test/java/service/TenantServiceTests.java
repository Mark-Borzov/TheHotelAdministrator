package service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import exception.tenant.NoTenantsInTheHotelException;
import exception.tenant.TenantAlreadyExistsException;
import exception.tenant.TenantSettleErrorException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import exception.tenant.TenantNotFoundException;
import exception.room.RoomNotFoundException;
import static org.mockito.Mockito.verify;
import dto.tenant.CreateTenantRequestDTO;
import static org.mockito.Mockito.when;
import dto.tenant.TenantResponseDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import model.room.RoomStatus;
import model.tenant.Tenant;
import java.time.LocalDate;
import java.util.Arrays;
import org.mockito.Mock;
import model.room.Room;
import java.util.List;
import dao.TenantDAO;
import dao.RoomDAO;

@ExtendWith(MockitoExtension.class)
public class TenantServiceTests {

    @Mock
    private RoomDAO roomDAO;

    @Mock
    private TenantDAO tenantDAO;

    @Mock
    @SuppressWarnings("unused")
    private RoomService roomService;

    @InjectMocks
    private TenantService tenantService;

    // Тест: "Метод для Заселения Жителя в Номер":
    // Результат: "Успешно":
    @Test
    public void settleTenantTest() {
        when(tenantDAO.existsByInn("123456789012")).thenReturn(false);
        when(roomDAO.findRoomByNumber(1)).thenReturn(new Room());
        CreateTenantRequestDTO request = new CreateTenantRequestDTO();
        request.setTenantName("Иван Иванов");
        request.setTenantINN("123456789012");
        request.setRoomNumber(1);
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setDateOfIssueOfTheRoom(LocalDate.now().plusDays(5));
        TenantResponseDTO response = tenantService.settleTenant(request);
        assertNotNull(response);
        assertEquals("Житель Успешно Добавлен в Номер:", response.getMessage());
        verify(roomDAO).updateRoomStatus(1, RoomStatus.INHABITED);
    }

    // Тест: "Метод для Заселения Жителя в Номер":
    // Результат: Исключение "TenantAlreadyExistsException":
    @Test
    public void settleTenantWhenTenantAlreadyExistsExceptionTest() {
        when(tenantDAO.existsByInn("123456789012")).thenReturn(true);
        CreateTenantRequestDTO request = new CreateTenantRequestDTO();
        request.setTenantName("Иван Иванов");
        request.setTenantINN("123456789012");
        request.setRoomNumber(1);
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setDateOfIssueOfTheRoom(LocalDate.now().plusDays(5));
        assertThrows(TenantAlreadyExistsException.class, () -> tenantService.settleTenant(request));
    }

    // Тест: "Метод для Заселения Жителя в Номер":
    // Результат: Исключение "TenantSettleErrorException":
    @Test
    public void settleTenantWhenTenantSettleErrorExceptionTest() {
        when(tenantDAO.existsByInn("123456789012")).thenReturn(false);
        CreateTenantRequestDTO request = new CreateTenantRequestDTO();
        request.setTenantName("Иван Иванов");
        request.setTenantINN("123456789012");
        request.setRoomNumber(1);
        request.setCheckInDate(LocalDate.now().minusDays(1));
        request.setDateOfIssueOfTheRoom(LocalDate.now().plusDays(5));
        assertThrows(TenantSettleErrorException.class, () -> tenantService.settleTenant(request));
    }

    // Тест: "Метод для Выселения Жителя из Номера":
    // Результат: "Успешно":
    @Test
    public void evictTenantTest() {
        when(tenantDAO.availabilityOfTenant()).thenReturn(false);
        when(roomDAO.findRoomByNumber(1)).thenReturn(new Room());
        Tenant tenant = new Tenant();
        tenant.setTenantName("Иван Иванов");
        tenant.setTenantINN("123456789012");
        when(tenantDAO.findTenantByInnAndRoomNumber("123456789012", 1)).thenReturn(tenant);
        TenantResponseDTO response = tenantService.evictTenant(1, "123456789012");
        assertNotNull(response);
        assertEquals("Житель Успешно Удален из Номера:", response.getMessage());
        verify(tenantDAO).removeTenant(tenant);
        verify(roomDAO).updateRoomStatus(1, RoomStatus.NOT_INHABITED);
    }

    // Тест: "Метод для Выселения Жителя из Номера":
    // Результат: Исключение "NoTenantsInTheHotelException":
    @Test
    public void evictTenantWhenNoTenantsInTheHotelExceptionTest() {
        when(tenantDAO.availabilityOfTenant()).thenReturn(true);
        assertThrows(NoTenantsInTheHotelException.class,
                () -> tenantService.evictTenant(1, "123456789012"));
    }

    // Тест: "Метод для Выселения Жителя из Номера":
    // Результат: Исключение "RoomNotFoundException":
    @Test
    public void evictTenantWhenRoomNotFoundExceptionTest() {
        when(tenantDAO.availabilityOfTenant()).thenReturn(false);
        when(roomDAO.findRoomByNumber(1)).thenReturn(null);
        assertThrows(RoomNotFoundException.class,
                () -> tenantService.evictTenant(1, "123456789012"));
    }

    // Тест: "Метод для Выселения Жителя из Номера":
    // Результат: Исключение "TenantNotFoundException":
    @Test
    public void evictTenantWhenTenantNotFoundExceptionTest() {
        when(tenantDAO.availabilityOfTenant()).thenReturn(false);
        when(roomDAO.findRoomByNumber(1)).thenReturn(new Room());
        when(tenantDAO.findTenantByInnAndRoomNumber("123456789012", 1)).thenReturn(null);
        assertThrows(TenantNotFoundException.class,
                () -> tenantService.evictTenant(1, "123456789012"));
    }

    // Тест: "Метод для Получения Списка Жителей и их Номеров (Отсортированных по Алфавиту)":
    // Результат: "Успешно":
    @Test
    public void getSortedTenantsByAlphabetTest() {
        when(tenantDAO.availabilityOfTenant()).thenReturn(false);
        Tenant tenant1 = new Tenant();
        tenant1.setTenantName("Иван Иванов");
        tenant1.setTenantINN("123456789012");
        tenant1.setRoom(new Room());
        Tenant tenant2 = new Tenant();
        tenant2.setTenantName("Петр Петров");
        tenant2.setTenantINN("987654321098");
        tenant2.setRoom(new Room());
        List<Tenant> sortedTenants = Arrays.asList(tenant1, tenant2);
        when(tenantDAO.getSortedTenants("Алфавит")).thenReturn(sortedTenants);
        List<TenantResponseDTO> tenantResponseDTOs = tenantService.getSortedTenants("Алфавит");
        assertNotNull(tenantResponseDTOs);
        assertEquals(2, tenantResponseDTOs.size());
        assertEquals("Информация о Жителе:", tenantResponseDTOs.get(0).getMessage());
        assertEquals("Иван Иванов", tenantResponseDTOs.get(0).getTenantName());
        assertEquals("Информация о Жителе:", tenantResponseDTOs.get(1).getMessage());
        assertEquals("Петр Петров", tenantResponseDTOs.get(1).getTenantName());
        verify(tenantDAO).getSortedTenants("Алфавит");
    }

    // Тест: "Метод для Получения Списка Жителей и их Номеров (Отсортированных по Дате Освобождения)":
    // Результат: "Успешно":
    @Test
    public void getSortedTenantsByReleaseDateTest() {
        when(tenantDAO.availabilityOfTenant()).thenReturn(false);
        Tenant tenant1 = new Tenant();
        tenant1.setTenantName("Иван Иванов");
        tenant1.setTenantINN("123456789012");
        tenant1.setDateOfIssueOfTheRoom(LocalDate.now().plusDays(5));
        tenant1.setRoom(new Room());
        Tenant tenant2 = new Tenant();
        tenant2.setTenantName("Петр Петров");
        tenant2.setTenantINN("987654321098");
        tenant2.setDateOfIssueOfTheRoom(LocalDate.now().plusDays(3));
        tenant2.setRoom(new Room());
        List<Tenant> sortedTenants = Arrays.asList(tenant1, tenant2);
        when(tenantDAO.getSortedTenants("Освобождение")).thenReturn(sortedTenants);
        List<TenantResponseDTO> tenantResponseDTOs = tenantService.getSortedTenants("Освобождение");
        assertNotNull(tenantResponseDTOs);
        assertEquals(2, tenantResponseDTOs.size());
        assertEquals("Информация о Жителе:", tenantResponseDTOs.get(0).getMessage());
        assertEquals("Иван Иванов", tenantResponseDTOs.get(0).getTenantName());
        assertEquals("Информация о Жителе:", tenantResponseDTOs.get(1).getMessage());
        assertEquals("Петр Петров", tenantResponseDTOs.get(1).getTenantName());
        verify(tenantDAO).getSortedTenants("Освобождение");
    }

    // Тест: "Метод для Получения Списка Жителей и их Номеров (Отсортированных по Дате Освобождения)":
    // Результат: Исключение "NoTenantsInTheHotelException":
    @Test
    public void getSortedTenantsNoTenantsInTheHotelExceptionTest() {
        when(tenantDAO.availabilityOfTenant()).thenReturn(true);
        assertThrows(NoTenantsInTheHotelException.class, () -> tenantService.getSortedTenants("Освобождение"));
    }

    // Тест: "Метод для Получения Оплаты Жителя за Номер и Услуги":
    // Результат: Успешно:
    @Test
    public void calculateFinalPaymentTest() {
        when(tenantDAO.availabilityOfTenant()).thenReturn(false);
        Tenant tenant = new Tenant();
        tenant.setTenantINN("123456789012");
        Room room = new Room();
        tenant.setRoom(room);
        tenant.setCheckInDate(LocalDate.now());
        tenant.setDateOfIssueOfTheRoom(LocalDate.now().plusDays(5));
        when(tenantDAO.findTenantByInn("123456789012")).thenReturn(tenant);
        double servicesPayment = 50.0;
        when(tenantDAO.getPaymentForTenant(tenant)).thenReturn(servicesPayment);
        double finalPayment = tenantService.calculateFinalPayment("123456789012");
        assertEquals(tenant.paymentForRoom(room) * 5 + servicesPayment, finalPayment, 0.01);
    }

    // Тест: "Метод для Получения Общего Количества Жителей":
    // Результат: Успешно:
    @Test
    public void getTotalTenantsTest() {
        long expectedCount = 5;
        when(tenantDAO.countAllTenants()).thenReturn(expectedCount);
        long actualCount = tenantService.getTotalTenants();
        assertEquals(expectedCount, actualCount);
    }
}