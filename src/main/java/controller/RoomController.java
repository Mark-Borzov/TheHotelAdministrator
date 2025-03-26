package controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.constraints.Min;
import dto.room.CreateRoomRequestDTO;
import lombok.extern.slf4j.Slf4j;
import dto.room.RoomResponseDTO;
import dto.room.RoomHistoryDTO;
import model.room.RoomStatus;
import service.RoomService;
import java.time.LocalDate;
import java.util.ArrayList;
import model.room.Room;
import java.util.List;
import dao.RoomDAO;

@Slf4j
@Validated
@RestController
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;
    private final RoomDAO roomDAO;

    @Autowired
    public RoomController(RoomService roomService, RoomDAO roomDAO) {
        this.roomService = roomService;
        this.roomDAO = roomDAO;
    }

    // Метод для Получения Общего Количества Номеров Гостиницы:
    @GetMapping("/rooms-count")
    public ResponseEntity<Long> getCountRooms() {
        log.info("Запрос на Получение Общего Количества Номеров Гостиницы.");
        long totalRoomsCount = roomService.getTotalRoomsCount();
        log.info("Общее количество Номеров: {}", totalRoomsCount);
        return ResponseEntity.ok(totalRoomsCount);
    }

    // Метод для Получения Общего Количества Свободных Номеров Гостиницы:
    @GetMapping("/available-rooms-count")
    public ResponseEntity<Long> getAvailableRooms() {
        log.info("Запрос на Получение Общего Количества Свободных Номеров Гостиницы.");
        long totalAvailableRoomsCount = roomService.getTotalAvailableRoomsCount();
        log.info("Общее Количество Свободных Номеров: {}", totalAvailableRoomsCount);
        return ResponseEntity.ok(totalAvailableRoomsCount);
    }

    // Метод для Получения Описания всех Номеров Гостиницы:
    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> descriptionHotelRooms() {
        log.info("Запрос на Получение Описания всех Номеров Гостиницы.");
        List<RoomResponseDTO> roomResponseDTOs = roomService.getRoomsInfo();
        log.info("Получено {} Номеров Гостиницы.", roomResponseDTOs.size());
        return ResponseEntity.ok(roomResponseDTOs);
    }

    // Метод для Создания Номера и его Добавления в Гостиницу:
    @PostMapping
    public ResponseEntity<RoomResponseDTO> creatingAndAddingRoom(
            @RequestBody @Validated CreateRoomRequestDTO request) {
        log.info("Запрос на Создание Номера.");
        Room room = roomService.createRoom(request);
        RoomResponseDTO responseDTO = new RoomResponseDTO("Созданный Номер:", room);
        log.info("Номер Успешно Создан: {}", room);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // Метод для Изменения Цены Номера:
    @PatchMapping("/changing-number")
    public ResponseEntity<RoomResponseDTO> changeRoomPrice(
            @RequestParam("roomNumber") int roomNumber,
            @RequestParam("newPrice") @Min(value = 1, message = "Цена Должна Быть не Менее 1") double newPrice) {
        log.info("Запрос на Изменение Цены Номера {} на Новую Цену: {}", roomNumber, newPrice);
        Room room = roomService.changeRoomPrice(roomNumber, newPrice);
        RoomResponseDTO responseDTO = new RoomResponseDTO("Цена Номера Изменена:", room);
        log.info("Цена Номера {} Успешно Изменена на: {}", roomNumber, newPrice);
        return ResponseEntity.ok(responseDTO);
    }

    // Метод для Получения Списка всех Номеров (Отсортированных по Передаваемому Параметру):
    @GetMapping("/sorted")
    public ResponseEntity<List<RoomResponseDTO>> sortingRooms(
            @RequestParam(name = "sortBy") String sortBy) {
        log.info("Запрос на Получение Списка всех Номеров, Отсортированных по Параметру: {}", sortBy);
        List<RoomResponseDTO> sortedRooms = roomService.getSortedRooms(sortBy);
        log.info("Получено {} Отсортированных Номеров.", sortedRooms.size());
        return ResponseEntity.ok(sortedRooms);
    }

    // Метод для Получения Списка Свободных Номеров (Отсортированных по Передаваемому Параметру):
    @GetMapping("/available-sorted")
    public ResponseEntity<List<RoomResponseDTO>> availableSortingRooms(
            @RequestParam(name = "sortBy") String sortBy) {
        log.info("Запрос на Получение Списка Свободных Номеров, Отсортированных по Параметру: {}", sortBy);
        List<RoomResponseDTO> sortedAvailableRooms = roomService.getSortedAvailableRooms(sortBy);
        log.info("Получено {} Отсортированных Свободных Номеров.", sortedAvailableRooms.size());
        return ResponseEntity.ok(sortedAvailableRooms);
    }

    // Метод для Получения Списка Номеров, которые будут Доступны по Определенной Дате:
    @GetMapping("/release-date")
    public ResponseEntity<List<RoomResponseDTO>> roomsAvailableOnSpecificDate(
            @RequestParam("date") LocalDate date) {
        log.info("Запрос на Получение Списка Номеров, Доступных на Дату: {}", date);
        List<Room> availableRooms = roomService.roomsAvailableOnSpecificDate(date);
        List<RoomResponseDTO> responseDTOs = new ArrayList<>();
        for (Room room : availableRooms) {
            responseDTOs.add(new RoomResponseDTO("Информация о Свободном Номере:", room));
        }
        log.info("Получено {} Свободных Номеров на Дату: {}", responseDTOs.size(), date);
        return ResponseEntity.ok(responseDTOs);
    }

    // Метод для Изменения Статуса Номера на "На Ремонте":
    @PatchMapping("/for-repair")
    public ResponseEntity<RoomResponseDTO> forRepair(
            @RequestParam("roomNumber") int roomNumber) {
        log.info("Запрос на Изменение Статуса Номера {} на 'На Ремонте'.", roomNumber);
        return this.changeRoomStatus(roomNumber, RoomStatus.FOR_REPAIR, "(На Ремонте)");
    }

    // Метод для Изменения Статуса Номера с "На Ремонте" на "Не Заселен":
    @PatchMapping("/not-inhabited")
    public ResponseEntity<RoomResponseDTO> toNotInhabited(
            @RequestParam("roomNumber") int roomNumber) {
        log.info("Запрос на Изменение Статуса Номера {} на 'Не Заселен'.", roomNumber);
        return this.changeRoomStatus(roomNumber, RoomStatus.NOT_INHABITED, "(Не Заселен)");
    }

    // Метод для Получения Списка Последних Жителей Номера (по Номеру Размещения):
    @GetMapping("/room-history")
    public ResponseEntity<List<RoomHistoryDTO>> lastTenantsHistory(
            @RequestParam("roomNumber") int roomNumber) {
        log.info("Запрос на Получение Истории Последних Жителей Номера {}.", roomNumber);
        List<RoomHistoryDTO> roomHistoryDTOs = roomService.getRoomHistory(roomNumber);
        log.info("Получено {} Записей Истории для Номера {}.", roomHistoryDTOs.size(), roomNumber);
        return ResponseEntity.ok(roomHistoryDTOs);
    }

    // Вспомогательный Метод для Изменения Статуса Номера:
    private ResponseEntity<RoomResponseDTO> changeRoomStatus(
            int roomNumber, RoomStatus newStatus, String successMessage) {
        log.info("Изменение Статуса Номера {} на: {}", roomNumber, newStatus);
        roomService.updateRoomStatus(roomNumber, newStatus);
        Room room = roomDAO.findRoomByNumber(roomNumber);
        RoomResponseDTO responseDTO = new RoomResponseDTO("Статус Номера Успешно Изменен на "
                + successMessage + ".", room);
        log.info("Статус Номера {} Успешно Изменен на: {}", roomNumber, newStatus);
        return ResponseEntity.ok(responseDTO);
    }
}