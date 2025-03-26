package service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import exception.tenant.TenantAlreadyExistsException;
import exception.tenant.NoTenantsInTheHotelException;
import exception.tenant.TenantSettleErrorException;
import exception.tenant.TenantNotFoundException;
import org.springframework.stereotype.Service;
import exception.room.RoomNotFoundException;
import dto.tenant.CreateTenantRequestDTO;
import dto.tenant.TenantResponseDTO;
import model.room.RoomStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import model.tenant.Tenant;
import model.room.Room;
import java.util.List;
import dao.TenantDAO;
import dao.RoomDAO;

@Service
public class TenantService {

    private final RoomService roomService;
    private final RoomDAO roomDAO;
    private final TenantDAO tenantDAO;

    @Autowired
    public TenantService(RoomDAO roomDAO, TenantDAO tenantDAO, RoomService roomService) {
        this.roomDAO = roomDAO;
        this.tenantDAO = tenantDAO;
        this.roomService = roomService;
    }

    // Метод для Заселения Жителя в Номер через "TenantService":
    @Transactional
    public TenantResponseDTO settleTenant(CreateTenantRequestDTO request) {
        roomService.checkHotelExists();
        roomService.checkRoomsExist();
        if (tenantDAO.existsByInn(request.getTenantINN())) {
            throw new TenantAlreadyExistsException(
                    "Ошибка: Житель с ИНН " + request.getTenantINN() + " уже Существует в Гостинице.");
        }
        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            throw new TenantSettleErrorException("Ошибка: Дата Заселения не Может быть Раньше Текущей Даты.");
        }
        if (request.getDateOfIssueOfTheRoom().isBefore(request.getCheckInDate())) {
            throw new TenantSettleErrorException("Ошибка: Дата Выселения не Может быть Раньше Даты Заселения.");
        }
        if (request.getDateOfIssueOfTheRoom().isEqual(request.getCheckInDate())) {
            throw new TenantSettleErrorException("Ошибка: Нельзя Заселиться и Выселиться в один День.");
        }
        Room room = roomDAO.findRoomByNumber(request.getRoomNumber());
        if (room == null) {
            throw new RoomNotFoundException("Ошибка: Номер: " + request.getRoomNumber() + " не Найден в Гостинице.");
        }
        Tenant tenant = new Tenant();
        tenant.setTenantName(request.getTenantName());
        tenant.setTenantINN(request.getTenantINN());
        tenant.setRoomNumber(request.getRoomNumber());
        tenant.setCheckInDate(request.getCheckInDate());
        tenant.setDateOfIssueOfTheRoom(request.getDateOfIssueOfTheRoom());
        tenant.setRoom(room);
        tenantDAO.addTenant(tenant);
        roomDAO.updateRoomStatus(request.getRoomNumber(), RoomStatus.INHABITED);
        return new TenantResponseDTO("Житель Успешно Добавлен в Номер:", tenant, room);
    }

    // Метод для Выселения Жителя из Номера через "TenantService":
    @Transactional
    public TenantResponseDTO evictTenant(int roomNumber, String tenantINN) {
        roomService.checkHotelExists();
        roomService.checkRoomsExist();
        if (tenantDAO.availabilityOfTenant()) {
            throw new NoTenantsInTheHotelException("Ошибка: В Гостинице нет Жителей.");
        }
        Room room = roomDAO.findRoomByNumber(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException("Ошибка: Номер: " + roomNumber + " не Найден в Гостинице.");
        }
        Tenant tenant = tenantDAO.findTenantByInnAndRoomNumber(tenantINN, roomNumber);
        if (tenant == null) {
            throw new TenantNotFoundException("Ошибка: Житель с ИНН " + tenantINN + " не Найден в Номере "
                    + roomNumber + ".");
        }
        tenantDAO.removeTenant(tenant);
        long currentOccupants = roomDAO.getCurrentOccupantsCount(roomNumber);
        if (currentOccupants == 0) {
            roomDAO.updateRoomStatus(roomNumber, RoomStatus.NOT_INHABITED);
        }
        return new TenantResponseDTO("Житель Успешно Удален из Номера:", tenant, room);
    }

    // Метод для Получения Списка Жителей и их Номеров (Отсортированных по Параметру) через "TenantService":
    @Transactional(readOnly = true)
    public List<TenantResponseDTO> getSortedTenants(String sortBy) {
        roomService.checkHotelExists();
        roomService.checkRoomsExist();
        if (tenantDAO.availabilityOfTenant()) {
            throw new NoTenantsInTheHotelException("Ошибка: В Гостинице нет Жителей.");
        }
        List<Tenant> sortedTenants = tenantDAO.getSortedTenants(sortBy);
        List<TenantResponseDTO> tenantResponseDTOs = new ArrayList<>();
        for (Tenant tenant : sortedTenants) {
            TenantResponseDTO dto = new TenantResponseDTO("Информация о Жителе:", tenant, tenant.getRoom());
            tenantResponseDTOs.add(dto);
        }
        return tenantResponseDTOs;
    }

    // Метод для Получения Оплаты Жителя за Номер и Услуги через "TenantService":
    @Transactional(readOnly = true)
    public double calculateFinalPayment(String tenantINN) {
        roomService.checkHotelExists();
        roomService.checkRoomsExist();
        if (tenantDAO.availabilityOfTenant()) {
            throw new NoTenantsInTheHotelException("Ошибка: В Гостинице нет Жителей.");
        }
        Tenant tenant = tenantDAO.findTenantByInn(tenantINN);
        if (tenant == null) {
            throw new TenantNotFoundException("Ошибка: Житель с ИНН " + tenantINN + " не Найден.");
        }
        Room room = tenant.getRoom();
        double roomPayment = tenant.paymentForRoom(room);
        double servicesPayment = tenantDAO.getPaymentForTenant(tenant);
        return roomPayment + servicesPayment;
    }

    // Метод для Получения Общего Количества Жителей через "TenantService":
    @Transactional(readOnly = true)
    public long getTotalTenants() {
        roomService.checkHotelExists();
        roomService.checkRoomsExist();
        return tenantDAO.countAllTenants();
    }
}