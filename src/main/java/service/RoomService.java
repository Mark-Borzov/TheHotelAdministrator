package service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import exception.room.RoomNotFoundOnSpecifiedDateException;
import org.springframework.beans.factory.annotation.Value;
import exception.room.RoomStatusCannotBeChangedException;
import exception.room.RoomHistoryNotFoundException;
import exception.room.NoRoomsInTheHotelException;
import exception.room.RoomAlreadyExistsException;
import org.springframework.stereotype.Service;
import exception.hotel.HotelNotFoundException;
import exception.room.RoomNotFoundException;
import dto.room.CreateRoomRequestDTO;
import dto.room.RoomResponseDTO;
import mapper.RoomHistoryMapper;
import dto.room.RoomHistoryDTO;
import model.room.RoomHistory;
import model.room.RoomStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import mapper.RoomMapper;
import model.hotel.Hotel;
import model.room.Room;
import java.util.List;
import dao.HotelDAO;
import dao.RoomDAO;

@Service
public class RoomService {

    // Параметр для Управления Изменением статуса Номеров:
    @Value("${Room.roomStatusChangeEnabled}")
    private boolean roomStatusChangeEnabled;

    // Параметр для Установки Количества записей о Жителях в Истории Номера:
    @Value("${Room.tenantHistorySize}")
    private int tenantHistorySize;

    private final HotelDAO hotelDAO;
    private final RoomDAO roomDAO;

    @Autowired
    public RoomService(HotelDAO hotelDAO, RoomDAO roomDAO) {
        this.hotelDAO = hotelDAO;
        this.roomDAO = roomDAO;
    }

    // Метод для Получения Общего Количества Номеров Гостиницы через "RoomService":
    @Transactional(readOnly = true)
    public long getTotalRoomsCount() {
        this.checkHotelExists();
        return roomDAO.totalRoomsCount();
    }

    // Метод для Получения Общего Количества Свободных Номеров Гостиницы через "RoomService":
    @Transactional(readOnly = true)
    public long getTotalAvailableRoomsCount() {
        this.checkHotelExists();
        return roomDAO.totalAvailableRoomsCount();
    }

    // Метод для Получения Описания всех Номеров Гостиницы через "RoomService":
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getRoomsInfo() {
        this.checkHotelExists();
        this.checkRoomsExist();
        List<Room> rooms = roomDAO.informationOfRooms();
        List<RoomResponseDTO> roomResponseDTOs = new ArrayList<>();
        for (Room room : rooms) {
            roomResponseDTOs.add(new RoomResponseDTO("Информация о Номере:", room));
        }
        return roomResponseDTOs;
    }

    // Метод для Создания Номера через "RoomService":
    @Transactional
    public Room createRoom(CreateRoomRequestDTO request) {
        this.checkHotelExists();
        Hotel hotel = hotelDAO.getHotel();
        if (roomDAO.doesRoomNumberExist(request.getRoomNumber())) {
            throw new RoomAlreadyExistsException("Ошибка: Данный Номер уже Существует.");
        }
        Room room = RoomMapper.INSTANCE.toRoom(request, this.roomStatusChangeEnabled, this.tenantHistorySize);
        room.setHotel(hotel);
        roomDAO.createRoom(room);
        return room;
    }

    // Метод для Изменения Цены Номера через "RoomService":
    @Transactional
    public Room changeRoomPrice(int roomNumber, double newPrice) {
        this.checkHotelExists();
        this.checkRoomsExist();
        Room room = roomDAO.findRoomByNumber(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException("Ошибка: Номер Размещения: " + roomNumber + " не Найден.");
        }
        room.setRoomPrice(newPrice);
        roomDAO.updateRoomPrice(room);
        return room;
    }

    // Метод для Получения Списка всех Номеров (Отсортированных по Параметру) через "RoomService":
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getSortedRooms(String sortBy) {
        this.checkHotelExists();
        this.checkRoomsExist();
        List<Room> sortedRooms = roomDAO.getSortedInformationOfRooms(sortBy);
        List<RoomResponseDTO> roomResponseDTOs = new ArrayList<>();
        for (Room room : sortedRooms) {
            roomResponseDTOs.add(new RoomResponseDTO("Информация о Номере:", room));
        }
        return roomResponseDTOs;
    }

    // Метод для Получения Списка Свободных Номеров (Отсортированных по Параметру) через "RoomService":
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getSortedAvailableRooms(String sortBy) {
        this.checkHotelExists();
        this.checkRoomsExist();
        List<Room> sortedAvailableRooms = roomDAO.getSortedInformationOfAvailableRooms(sortBy);
        List<RoomResponseDTO> roomResponseDTOs = new ArrayList<>();
        for (Room room : sortedAvailableRooms) {
            roomResponseDTOs.add(new RoomResponseDTO("Информация о Свободном Номере:", room));
        }
        return roomResponseDTOs;
    }

    // Метод для Получения Списка Номеров, которые будут Доступны по Определенной Дате через "RoomService":
    @Transactional(readOnly = true)
    public List<Room> roomsAvailableOnSpecificDate(LocalDate date) {
        this.checkHotelExists();
        this.checkRoomsExist();
        List<Room> availableRooms = roomDAO.roomsAvailableOnSpecificDate(date);
        if (availableRooms.isEmpty()) {
            throw new RoomNotFoundOnSpecifiedDateException(
                    "Ошибка: По Указанной Дате нет Доступных Номеров.");
        }
        return availableRooms;
    }

    // Вспомогательный Метод для Изменения Статуса Номера через "RoomService":
    @Transactional
    public void updateRoomStatus(int roomNumber, RoomStatus newStatus) {
        this.checkHotelExists();
        this.checkRoomsExist();
        if (this.roomStatusChangeEnabled) {
            Room room = roomDAO.findRoomByNumber(roomNumber);
            if (room == null) {
                throw new RoomNotFoundException("Ошибка: Номер Размещения: " + roomNumber + " не Найден.");
            }
            if (room.getRoomStatus() == RoomStatus.INHABITED) {
                throw new RoomStatusCannotBeChangedException(
                        "Ошибка: Нельзя Изменить Статус Номера, так как он Заселен.");
            }
            if (room.getRoomStatus() == newStatus) {
                throw new RoomStatusCannotBeChangedException(
                        "Ошибка: Нельзя Изменить Статус Номера, так как он уже " + newStatus + ".");
            }
            roomDAO.updateRoomStatus(room.getRoomNumber(), newStatus);
        } else {
            throw new RoomStatusCannotBeChangedException("Ошибка: Нельзя Изменить Статус для Номера: " + roomNumber);
        }
    }

    // Метод для Получения Списка Последних Жителей Номера (по Номеру Размещения) через "RoomService":
    @Transactional(readOnly = true)
    public List<RoomHistoryDTO> getRoomHistory(int roomNumber) {
        this.checkHotelExists();
        this.checkRoomsExist();
        Room room = roomDAO.findRoomByNumber(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException("Ошибка: Номер Размещения: " + roomNumber + " не Найден.");
        }
        List<RoomHistory> roomHistories = roomDAO.getRoomHistory(roomNumber);
        if (roomHistories.isEmpty()) {
            throw new RoomHistoryNotFoundException("Ошибка: Нет Записей в Истории для Номера: " + roomNumber);
        }
        return this.convertToRoomHistoryDTOs(roomHistories);
    }

    // Метод для Проверки Существования Гостиницы через "RoomService":
    @Transactional(readOnly = true)
    public void checkHotelExists() {
        if (hotelDAO.getHotel() == null) {
            throw new HotelNotFoundException("Ошибка: Гостиница Не Найдена в Базе Данных.");
        }
    }

    // Метод для Проверки Существования Номеров в Гостинице через "RoomService":
    @Transactional(readOnly = true)
    public void checkRoomsExist() {
        if (roomDAO.totalRoomsCount() == 0) {
            throw new NoRoomsInTheHotelException("Ошибка: В Гостинице нет Номеров.");
        }
    }

    // Вспомогательный Метод для Получения Списка Последних Жителей Номера через "RoomService":
    private List<RoomHistoryDTO> convertToRoomHistoryDTOs(List<RoomHistory> roomHistories) {
        List<RoomHistoryDTO> roomHistoryDTOs = new ArrayList<>();
        for (RoomHistory roomHistory : roomHistories) {
            RoomHistoryDTO roomHistoryDTO = RoomHistoryMapper.INSTANCE.toRoomHistoryDTO(roomHistory);
            roomHistoryDTOs.add(roomHistoryDTO);
        }
        return roomHistoryDTOs;
    }
}