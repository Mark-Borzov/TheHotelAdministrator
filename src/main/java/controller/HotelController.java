package controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import dto.hotel.HotelResponseDTO;
import lombok.extern.slf4j.Slf4j;
import service.HotelService;
import model.hotel.Hotel;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/hotel")
public class HotelController {

    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    // Метод для Создания Гостиницы:
    @PostMapping
    public ResponseEntity<HotelResponseDTO> createHotel(@RequestBody Hotel hotel) {
        log.info("Запрос на Создание Гостиницы: {}", hotel.getHotelName());
        Hotel createdHotel = hotelService.createHotel(hotel);
        log.info("Гостиница \"{}\" Успешно Создана.", createdHotel.getHotelName());
        return createHorelResponseEntity("Гостиница Успешно Создана.", createdHotel.getHotelName(),
                createdHotel.getHotelID(), HttpStatus.CREATED);
    }

    // Метод для Получения Названия Гостиницы:
    @GetMapping("/hotel-name")
    public ResponseEntity<String> getHotelName() {
        log.info("Запрос на Получение Названия Гостиницы.");
        Hotel hotel = hotelService.checkAndGetHotel();
        log.info("Получено Название Гостиницы: {}", hotel.getHotelName());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(hotel.getHotelName());
    }

    // Метод для Изменения Названия Гостиницы:
    @PatchMapping
    public ResponseEntity<HotelResponseDTO> renameHotel(@RequestBody Hotel hotel) {
        log.info("Запрос на Изменение Названия Гостиницы на: {}", hotel.getHotelName());
        Hotel renamedHotel = hotelService.renameHotel(hotel);
        log.info("Гостиница Успешно Переименована в: {}", renamedHotel.getHotelName());
        return createHorelResponseEntity("Гостиница Успешно Переименована.", renamedHotel.getHotelName(),
                renamedHotel.getHotelID(), HttpStatus.OK);
    }

    // Метод для Получения Информации о Гостинице:
    @GetMapping
    public ResponseEntity<HotelResponseDTO> informationAboutHotel() {
        log.info("Запрос на Получение Информации о Гостинице.");
        HotelResponseDTO hotelInfo = hotelService.getHotelInfo();
        log.info("Информация о Гостинице: \nНазвание: {}\nID: {}", hotelInfo.getHotelName(), hotelInfo.getHotelID());
        return createHorelResponseEntity(hotelInfo.getMessage(), hotelInfo.getHotelName(),
                hotelInfo.getHotelID(), HttpStatus.OK);
    }

    // Метод Создания Ответа для Сущности Гостиница:
    private ResponseEntity<HotelResponseDTO> createHorelResponseEntity(
            String message, String hotelName, UUID hotelID, HttpStatus status) {
        HotelResponseDTO responseDTO = new HotelResponseDTO(message, hotelName, hotelID);
        return ResponseEntity.status(status).body(responseDTO);
    }
}