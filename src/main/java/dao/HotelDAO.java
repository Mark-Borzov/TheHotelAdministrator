package dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.hibernate.SessionFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import model.hotel.Hotel;

@Slf4j
@Repository
public class HotelDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public HotelDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Метод для Добавления Гостиницы в Базу Данных:
    public void addHotel(Hotel hotel) {
        log.info("Запрос на Добавление Гостиницы: {} в Базу Данных.", hotel.getHotelName());
        Session session = sessionFactory.getCurrentSession();
        session.persist(hotel);
        log.info("Гостиница \"{}\" Успешно Добавлена в Базу Данных.", hotel.getHotelName());
    }

    // Метод для Получения Гостиницы из Базы Данных:
    public Hotel getHotel() {
        Session session = sessionFactory.getCurrentSession();
        log.info("Запрос на Получение Информации о Гостинице из Базы Данных.");
        Hotel hotel = session.createQuery("FROM Hotel", Hotel.class).uniqueResult();
        if (hotel != null) {
            log.info("Гостиница \"{}\" Успешно Найдена в Базе Данных.", hotel.getHotelName());
        } else {
            log.warn("Гостиница Не Найдена в Базе Данных.");
        }
        return hotel;
    }

    // Метод для Изменения Имени Гостиницы в Базе Данных:
    public void updateHotelName(Hotel existingHotel) {
        log.info("Запрос на Изменение Имени Гостиницы: {}", existingHotel.getHotelName());
        Session session = sessionFactory.getCurrentSession();
        session.merge(existingHotel);
        log.info("Новое Имя Гостиницы: {}", this.getHotel().getHotelName());
    }
}