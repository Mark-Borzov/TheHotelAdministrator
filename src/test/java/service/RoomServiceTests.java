package service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import exception.room.RoomNotFoundOnSpecifiedDateException;
import org.springframework.beans.factory.annotation.Value;
import exception.room.RoomStatusCannotBeChangedException;
import exception.room. RoomHistoryNotFoundException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import exception.room.RoomAlreadyExistsException;
import static org.mockito.ArgumentMatchers.any;
import exception.room.RoomNotFoundException;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.when;
import dto.room.CreateRoomRequestDTO;
import org.junit.jupiter.api.Test;
import dto.room.RoomResponseDTO;
import dto.room.RoomHistoryDTO;
import org.mockito.InjectMocks;
import model.room.RoomHistory;
import model.room.RoomStatus;
import java.util.Collections;
import model.room.RoomType;
import java.time.LocalDate;
import model.hotel.Hotel;
import org.mockito.Mock;
import java.util.Arrays;
import model.room.Room;
import java.util.List;
import java.util.UUID;
import dao.HotelDAO;
import dao.RoomDAO;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTests {

    // Параметр для Управления Изменением статуса Номеров:
    @Value("${Room.roomStatusChangeEnabled}")
    private boolean roomStatusChangeEnabled;

    @Mock
    private HotelDAO hotelDAO;

    @Mock
    private RoomDAO roomDAO;

    @InjectMocks
    private RoomService roomService;

    private Hotel hotel;

    // Метод для Создания Тестовой Сущности Гостиницы:
    @BeforeEach
    public void setHotelEntity() {
        this.hotel = new Hotel();
        this.hotel.setHotelName("TestHotel");
        this.hotel.setHotelID(UUID.fromString("4c877fc9-a293-42a5-afc3-989afbf34a0f"));
    }

    // Тест: "Метод для Получения Общего Количества Номеров Гостиницы":
    // Результат: "Успешно":
    @Test
    public void getTotalRoomsCountTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(10L);
        long totalRoomsCount = roomService.getTotalRoomsCount();
        assertEquals(10L, totalRoomsCount);
        verify(roomDAO).totalRoomsCount();
    }

    // Тест: "Метод для Получения Общего Количества Свободных Номеров Гостиницы":
    // Результат: "Успешно":
    @Test
    public void getTotalAvailableRoomsCountTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalAvailableRoomsCount()).thenReturn(5L);
        long totalAvailableRoomsCount = roomService.getTotalAvailableRoomsCount();
        assertEquals(5L, totalAvailableRoomsCount);
        verify(roomDAO).totalAvailableRoomsCount();
    }

    // Тест: "Метод для Получения Описания всех Номеров Гостиницы":
    // Результат: "Успешно":
    @Test
    public void getRoomsInfoTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(2L);
        Room roomOne = new Room();
        roomOne.setRoomID(UUID.fromString("10600c02-7e08-445e-a730-300810592557"));
        roomOne.setRoomNumber(1);
        roomOne.setRoomType(RoomType.STUDIO);
        roomOne.setRoomStatus(RoomStatus.NOT_INHABITED);
        roomOne.setRoomPrice(3540);
        roomOne.setMaxCapacity(2);
        roomOne.setCountOfStars(2);
        Room roomTwo = new Room();
        roomTwo.setRoomID(UUID.fromString("b68a3a08-51ef-4c00-8074-44df53fde0ad"));
        roomTwo.setRoomNumber(2);
        roomTwo.setRoomType(RoomType.APARTMENT);
        roomTwo.setRoomStatus(RoomStatus.NOT_INHABITED);
        roomTwo.setRoomPrice(10500);
        roomTwo.setMaxCapacity(3);
        roomTwo.setCountOfStars(5);
        when(roomDAO.informationOfRooms()).thenReturn(Arrays.asList(roomOne, roomTwo));
        List<RoomResponseDTO> roomsInfo = roomService.getRoomsInfo();
        assertNotNull(roomsInfo);
        assertEquals(2, roomsInfo.size());
        verify(roomDAO).informationOfRooms();
        RoomResponseDTO dtoOne = roomsInfo.get(0);
        assertEquals("Информация о Номере:", dtoOne.getMessage());
        assertEquals(roomOne.getRoomID(), dtoOne.getRoomID());
        assertEquals(roomOne.getRoomNumber(), dtoOne.getRoomNumber());
        assertEquals(roomOne.getRoomType(), dtoOne.getRoomType());
        assertEquals(roomOne.getRoomStatus(), dtoOne.getRoomStatus());
        assertEquals(roomOne.getRoomPrice(), dtoOne.getRoomPrice(), 0.01);
        assertEquals(roomOne.getMaxCapacity(), dtoOne.getMaxCapacity());
        assertEquals(roomOne.getCountOfStars(), dtoOne.getCountOfStars());
        RoomResponseDTO dtoTwo = roomsInfo.get(1);
        assertEquals("Информация о Номере:", dtoTwo.getMessage());
        assertEquals(roomTwo.getRoomID(), dtoTwo.getRoomID());
        assertEquals(roomTwo.getRoomNumber(), dtoTwo.getRoomNumber());
        assertEquals(roomTwo.getRoomType(), dtoTwo.getRoomType());
        assertEquals(roomTwo.getRoomStatus(), dtoTwo.getRoomStatus());
        assertEquals(roomTwo.getRoomPrice(), dtoTwo.getRoomPrice(), 0.01);
        assertEquals(roomTwo.getMaxCapacity(), dtoTwo.getMaxCapacity());
        assertEquals(roomTwo.getCountOfStars(), dtoTwo.getCountOfStars());
    }

    // Тест: "Метод для Создания Номера":
    // Результат: "Успешно":
    @Test
    public void createRoomTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.doesRoomNumberExist(1)).thenReturn(false);
        CreateRoomRequestDTO request = new CreateRoomRequestDTO();
        request.setRoomNumber(1);
        request.setRoomType(RoomType.STUDIO);
        request.setRoomPrice(3540);
        request.setMaxCapacity(2);
        request.setCountOfStars(2);
        Room createdRoom = roomService.createRoom(request);
        assertNotNull(createdRoom);
        assertEquals(request.getRoomNumber(), createdRoom.getRoomNumber());
        assertEquals(request.getRoomType(), createdRoom.getRoomType());
        assertEquals(request.getRoomPrice(), createdRoom.getRoomPrice(), 0.01);
        assertEquals(request.getMaxCapacity(), createdRoom.getMaxCapacity());
        assertEquals(request.getCountOfStars(), createdRoom.getCountOfStars());
        verify(roomDAO).createRoom(any(Room.class));
        verify(roomDAO).doesRoomNumberExist(request.getRoomNumber());
    }

    // Тест: "Метод для Создания Номера":
    // Результат: Исключение "RoomAlreadyExistsException":
    @Test
    public void createRoomWhenRoomAlreadyExistsExceptionTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.doesRoomNumberExist(1)).thenReturn(true);
        CreateRoomRequestDTO request = new CreateRoomRequestDTO();
        request.setRoomNumber(1);
        assertThrows(RoomAlreadyExistsException.class, () -> roomService.createRoom(request));
        verify(roomDAO).doesRoomNumberExist(request.getRoomNumber());
    }

    // Тест: "Метод для Изменения Цены Номера":
    // Результат: "Успешно":
    @Test
    public void changeRoomPriceTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(1L);
        when(roomDAO.findRoomByNumber(1)).thenReturn(new Room());
        Room room = new Room();
        room.setRoomNumber(1);
        room.setRoomPrice(5000);
        double newPrice = 6000;
        Room updatedRoom = roomService.changeRoomPrice(1, newPrice);
        assertNotNull(updatedRoom);
        assertEquals(newPrice, updatedRoom.getRoomPrice(), 0.01);
        verify(roomDAO).updateRoomPrice(updatedRoom);
        verify(roomDAO).findRoomByNumber(1);
    }

    // Тест: "Метод для Изменения Цены Номера":
    // Результат: Исключение "RoomNotFoundException":
    @Test
    public void changeRoomPriceWhenRoomNotFoundExceptionTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(1L);
        when(roomDAO.findRoomByNumber(1)).thenReturn(null);
        assertThrows(RoomNotFoundException.class, () -> roomService.changeRoomPrice(1, 6000));
        verify(roomDAO).findRoomByNumber(1);
    }

    // Тест: "Метод для Получения Списка всех Номеров (Отсортированных по Цене)":
    // Результат: "Успешно":
    @Test
    public void getSortedRoomsByPriceTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(2L);
        when(roomDAO.getSortedInformationOfRooms("Цена"))
                .thenReturn(Arrays.asList(new Room(), new Room()));
        List<RoomResponseDTO> sortedRooms = roomService.getSortedRooms("Цена");
        assertNotNull(sortedRooms);
        assertEquals(2, sortedRooms.size());
        verify(roomDAO).getSortedInformationOfRooms("Цена");
    }

    // Тест: "Метод для Получения Списка всех Номеров (Отсортированных по Вместимости)":
    // Результат: "Успешно":
    @Test
    public void getSortedRoomsByCapacityTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(2L);
        when(roomDAO.getSortedInformationOfRooms("Вместимость"))
                .thenReturn(Arrays.asList(new Room(), new Room()));
        List<RoomResponseDTO> sortedRooms = roomService.getSortedRooms("Вместимость");
        assertNotNull(sortedRooms);
        assertEquals(2, sortedRooms.size());
        verify(roomDAO).getSortedInformationOfRooms("Вместимость");
    }

    // Тест: "Метод для Получения Списка всех Номеров (Отсортированных по Количеству Звезд)":
    // Результат: "Успешно":
    @Test
    public void getSortedRoomsByStarsTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(2L);
        when(roomDAO.getSortedInformationOfRooms("Звезды"))
                .thenReturn(Arrays.asList(new Room(), new Room()));
        List<RoomResponseDTO> sortedRooms = roomService.getSortedRooms("Звезды");
        assertNotNull(sortedRooms);
        assertEquals(2, sortedRooms.size());
        verify(roomDAO).getSortedInformationOfRooms("Звезды");
    }

    // Тест: "Метод для Получения Списка Свободных Номеров (Отсортированных по Цене)":
    // Результат: "Успешно":
    @Test
    public void getSortedAvailableRoomsByPriceTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(2L);
        when(roomDAO.getSortedInformationOfAvailableRooms("Цена"))
                .thenReturn(Arrays.asList(new Room(), new Room()));
        List<RoomResponseDTO> sortedAvailableRooms = roomService.getSortedAvailableRooms("Цена");
        assertNotNull(sortedAvailableRooms);
        assertEquals(2, sortedAvailableRooms.size());
        verify(roomDAO).getSortedInformationOfAvailableRooms("Цена");
    }

    // Тест: "Метод для Получения Списка Свободных Номеров (Отсортированных по Вместимости)":
    // Результат: "Успешно":
    @Test
    public void getSortedAvailableRoomsByCapacityTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(2L);
        when(roomDAO.getSortedInformationOfAvailableRooms("Вместимость"))
                .thenReturn(Arrays.asList(new Room(), new Room()));
        List<RoomResponseDTO> sortedAvailableRooms = roomService.getSortedAvailableRooms("Вместимость");
        assertNotNull(sortedAvailableRooms);
        assertEquals(2, sortedAvailableRooms.size());
        verify(roomDAO).getSortedInformationOfAvailableRooms("Вместимость");
    }

    // Тест: "Метод для Получения Списка Свободных Номеров (Отсортированных по Количеству Звезд)":
    // Результат: "Успешно":
    @Test
    public void getSortedAvailableRoomsByStarsTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(2L);
        when(roomDAO.getSortedInformationOfAvailableRooms("Звезды"))
                .thenReturn(Arrays.asList(new Room(), new Room()));
        List<RoomResponseDTO> sortedAvailableRooms = roomService.getSortedAvailableRooms("Звезды");
        assertNotNull(sortedAvailableRooms);
        assertEquals(2, sortedAvailableRooms.size());
        verify(roomDAO).getSortedInformationOfAvailableRooms("Звезды");
    }

    // Тест: "Метод для Получения Списка Номеров, которые будут Доступны по Определенной Дате":
    // Результат: "Успешно":
    @Test
    public void roomsAvailableOnSpecificDateTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(2L);
        when(roomDAO.roomsAvailableOnSpecificDate(LocalDate.of(2025, 10, 15)))
                .thenReturn(Arrays.asList(new Room(), new Room()));
        List<Room> availableRooms =
                roomService.roomsAvailableOnSpecificDate(LocalDate.of(2025, 10, 15));
        assertNotNull(availableRooms);
        assertEquals(2, availableRooms.size());
        verify(roomDAO).roomsAvailableOnSpecificDate(LocalDate.of(2025, 10, 15));
    }

    // Тест: "Метод для Получения Списка Номеров, которые будут Доступны по Определенной Дате":
    // Результат: Исключение "RoomNotFoundOnSpecifiedDateException":
    @Test
    public void roomsAvailableOnSpecificDateRoomNotFoundOnSpecifiedDateExceptionTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(1L);
        when(roomDAO.roomsAvailableOnSpecificDate(LocalDate.of(2025, 10, 15)))
                .thenReturn(Collections.emptyList());
        assertThrows(RoomNotFoundOnSpecifiedDateException.class,
                ()-> roomService.roomsAvailableOnSpecificDate(LocalDate.of(2025, 10, 15)));
        verify(roomDAO).roomsAvailableOnSpecificDate(LocalDate.of(2025, 10, 15));
    }

    // Тест: "Вспомогательный Метод для Изменения Статуса Номера":
    // Результат: "Успешно":
    @Test
    public void updateRoomStatusTest() {
        Room room = new Room();
        room.setRoomStatus(RoomStatus.INHABITED);
        if (this.roomStatusChangeEnabled) {
            roomService.updateRoomStatus(1, RoomStatus.INHABITED);
            assertEquals(RoomStatus.INHABITED, room.getRoomStatus());
            verify(roomDAO).updateRoomStatus(1, RoomStatus.INHABITED);
        }
    }

    // Тест: "Вспомогательный Метод для Изменения Статуса Номера":
    // Результат: Исключение "RoomNotFoundException":
    @Test
    public void updateRoomStatusRoomNotFoundExceptionTest() {
        if (this.roomStatusChangeEnabled) {
            assertThrows(RoomNotFoundException.class,
                    () -> roomService.updateRoomStatus(1, RoomStatus.NOT_INHABITED));
        }
    }

    // Тест: "Вспомогательный Метод для Изменения Статуса Номера":
    // Результат: Исключение "RoomStatusCannotBeChangedException":
    @Test
    public void updateRoomStatusRoomStatusCannotBeChangedExceptionTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(1L);
        Room room = new Room();
        room.setRoomStatus(RoomStatus.INHABITED);
        assertThrows(RoomStatusCannotBeChangedException.class, () -> roomService
                .updateRoomStatus(1, RoomStatus.NOT_INHABITED));
    }

    // Тест: "Метод для Получения Списка Последних Жителей Номера (по Номеру Размещения)":
    // Результат: "Успешно":
    @Test
    public void getRoomHistoryTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(2L);
        when(roomDAO.findRoomByNumber(1)).thenReturn(new Room());
        List<RoomHistory> roomHistories = Arrays.asList(new RoomHistory(), new RoomHistory());
        when(roomDAO.getRoomHistory(1)).thenReturn(roomHistories);
        List<RoomHistoryDTO> historyDTOs = roomService.getRoomHistory(1);
        assertNotNull(historyDTOs);
        assertEquals(2, historyDTOs.size());
        verify(roomDAO).getRoomHistory(1);
    }

    // Тест: "Метод для Получения Списка Последних Жителей Номера (по Номеру Размещения)":
    // Результат: Исключение "RoomNotFoundException":
    @Test
    public void getRoomHistoryWhenRoomNotFoundTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(2L);
        when(roomDAO.findRoomByNumber(1)).thenReturn(null);
        assertThrows(RoomNotFoundException.class, () -> roomService.getRoomHistory(1));
    }

    // Тест: "Метод для Получения Списка Последних Жителей Номера (по Номеру Размещения)":
    // Результат: Исключение "RoomHistoryNotFoundException":
    @Test
    public void getRoomHistoryWhenRoomHistoryNotFoundExceptionTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        when(roomDAO.totalRoomsCount()).thenReturn(2L);
        when(roomDAO.findRoomByNumber(1)).thenReturn(new Room());
        when(roomDAO.getRoomHistory(1)).thenReturn(Collections.emptyList());
        assertThrows(RoomHistoryNotFoundException.class, () -> roomService.getRoomHistory(1));
    }
}