package service;

import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import exception.hotel.HotelAlreadyExistsException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import exception.hotel.HotelNotFoundException;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.when;
import dto.hotel.HotelResponseDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import model.hotel.Hotel;
import org.mockito.Mock;
import java.util.UUID;
import dao.HotelDAO;

@ExtendWith(MockitoExtension.class)
public class HotelServiceTests {

    @Mock
    private HotelDAO hotelDAO;

    @InjectMocks
    private HotelService hotelService;

    private Hotel hotel;

    // Метод для Создания Тестовой Сущности Гостиницы:
    @BeforeEach
    public void setHotelEntity() {
        this.hotel = new Hotel();
        this.hotel.setHotelName("TestHotel");
        this.hotel.setHotelID(UUID.fromString("4c877fc9-a293-42a5-afc3-989afbf34a0f"));
    }

    // Тест: "Метод для Создания Гостиницы":
    // Результат: "Успешно":
    @Test
    public void createHotelTest() {
        when(hotelDAO.getHotel()).thenReturn(null);
        Hotel createdHotel = hotelService.createHotel(hotel);
        assertNotNull(createdHotel);
        assertEquals(hotel.getHotelName(), createdHotel.getHotelName());
        assertEquals(hotel.getHotelID(), createdHotel.getHotelID());
        verify(hotelDAO).addHotel(hotel);
    }

    // Тест: "Метод Создания Гостиницы":
    // Результат: Исключение "HotelAlreadyExistsException":
    @Test
    public void createHotelWhenHotelAlreadyExistsExceptionTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        assertThrows(HotelAlreadyExistsException.class, () -> hotelService.createHotel(hotel));
        verify(hotelDAO).getHotel();
    }

    // Тест: "Метод для Получения и Проверки Существования Гостиницы":
    // Результат: Успешно:
    @Test
    public void checkAndGetHotelTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        Hotel retrievedHotel = hotelService.checkAndGetHotel();
        assertNotNull(retrievedHotel);
        assertEquals(hotel.getHotelName(), retrievedHotel.getHotelName());
        assertEquals(hotel.getHotelID(), retrievedHotel.getHotelID());
        verify(hotelDAO).getHotel();
    }

    // Тест: "Метод для Получения и Проверки Существования Гостиницы":
    // Результат: Исключение "HotelNotFoundException":
    @Test
    public void checkAndGetHotelWhenHotelNotFoundExceptionTest() {
        when(hotelDAO.getHotel()).thenReturn(null);
        assertThrows(HotelNotFoundException.class, () -> hotelService.checkAndGetHotel());
        verify(hotelDAO).getHotel();
    }

    // Тест: "Метод для Изменения Названия Гостиницы":
    // Результат: Успешно:
    @Test
    public void renameHotelTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        Hotel updatedHotel = new Hotel();
        updatedHotel.setHotelName("UpdatedHotel");
        updatedHotel.setHotelID(hotel.getHotelID());
        Hotel resultHotel = hotelService.renameHotel(updatedHotel);
        assertEquals("UpdatedHotel", resultHotel.getHotelName());
        assertEquals(updatedHotel.getHotelID(), resultHotel.getHotelID());
        verify(hotelDAO).getHotel();
        verify(hotelDAO).updateHotelName(hotel);
    }

    // Тест: "Метод для Изменения Названия Гостиницы":
    // Результат: Исключение "HotelNotFoundException":
    @Test
    public void renameHotelWhenHotelNotFoundExceptionTest() {
        when(hotelDAO.getHotel()).thenReturn(null);
        assertThrows(HotelNotFoundException.class, () -> hotelService.renameHotel(hotel));
        verify(hotelDAO).getHotel();
    }

    // Тест: "Метод для Получения Информации о Гостинице":
    // Результат: Успешно:
    @Test
    public void getHotelInfoTest() {
        when(hotelDAO.getHotel()).thenReturn(hotel);
        HotelResponseDTO hotelInfo = hotelService.getHotelInfo();
        assertNotNull(hotelInfo);
        assertEquals("Информация о Гостинице.", hotelInfo.getMessage());
        assertEquals(hotel.getHotelName(), hotelInfo.getHotelName());
        assertEquals(hotel.getHotelID(), hotelInfo.getHotelID());
        verify(hotelDAO).getHotel();
    }

    // Тест: "Метод для Получения Информации о Гостинице":
    // Результат: Исключение "HotelNotFoundException":
    @Test
    public void getHotelInfoWhenHotelNotFoundExceptionTest() {
        when(hotelDAO.getHotel()).thenReturn(null);
        assertThrows(HotelNotFoundException.class, () -> hotelService.getHotelInfo());
        verify(hotelDAO).getHotel();
    }
}