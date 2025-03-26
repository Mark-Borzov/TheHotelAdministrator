package service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import exception.hotel.HotelAlreadyExistsException;
import exception.hotel.HotelNotFoundException;
import org.springframework.stereotype.Service;
import dto.hotel.HotelResponseDTO;
import model.hotel.Hotel;
import dao.HotelDAO;

@Service
public class HotelService {

    private final HotelDAO hotelDAO;

    @Autowired
    public HotelService(HotelDAO hotelDAO) {
        this.hotelDAO = hotelDAO;
    }

    // Метод для Создания Гостиницы через "HotelService":
    @Transactional
    public Hotel createHotel(Hotel hotel) {
        if (hotelDAO.getHotel() != null) {
            throw new HotelAlreadyExistsException("Ошибка: Гостиница уже Существует в Базе Данных.");
        }
        hotelDAO.addHotel(hotel);
        return hotel;
    }

    // Метод для Получения и Проверки Существования Гостиницы через "HotelService":
    @Transactional(readOnly = true)
    public Hotel checkAndGetHotel() {
        Hotel hotel = hotelDAO.getHotel();
        if (hotel == null) {
            throw new HotelNotFoundException("Ошибка: Гостиница Не Найдена в Базе Данных.");
        }
        return hotel;
    }

    // Метод для Изменения Названия Гостиницы через "HotelService":
    @Transactional
    public Hotel renameHotel(Hotel hotel) {
        Hotel existingHotel = this.checkAndGetHotel();
        existingHotel.setHotelName(hotel.getHotelName());
        hotelDAO.updateHotelName(existingHotel);
        return existingHotel;
    }

    // Метод для Получения Информации о Гостинице через "HotelService":
    @Transactional(readOnly = true)
    public HotelResponseDTO getHotelInfo() {
        Hotel hotel = this.checkAndGetHotel();
        return new HotelResponseDTO("Информация о Гостинице.", hotel.getHotelName(), hotel.getHotelID());
    }
}