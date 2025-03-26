package dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.hibernate.SessionFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Query;
import model.room.RoomHistory;
import model.room.RoomStatus;
import org.hibernate.Session;
import java.time.LocalDate;
import model.room.Room;
import java.util.List;

@Slf4j
@Repository
public class RoomDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public RoomDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Метод для Получения Общего Количества Номеров из Базы Данных:
    public long totalRoomsCount() {
        Session session = sessionFactory.getCurrentSession();
        log.info("Получение Общего Количества Номеров Гостиницы из Базы Данных.");
        Query<Long> query = session.createQuery("SELECT COUNT(r) FROM Room r", Long.class);
        Long count = query.uniqueResult();
        return count != null ? count : 0;
    }

    // Метод для Получения Общего Количества Свободных Номеров из Базы Данных:
    public long totalAvailableRoomsCount() {
        Session session = sessionFactory.getCurrentSession();
        log.info("Получение Общего Количества Свободных Номеров в Гостинице из Базы Данных.");
        String hql = "SELECT COUNT(r) FROM Room r WHERE r.roomStatus = :roomStatus";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("roomStatus", RoomStatus.NOT_INHABITED);
        Long count = query.uniqueResult();
        return count != null ? count : 0;
    }

    // Метод для Вывода Описания всех Номеров Гостиницы из Базы Данных:
    public List<Room> informationOfRooms() {
        Session session = sessionFactory.getCurrentSession();
        log.info("Получение Описания всех Номеров в Гостинице из Базы Данных.");
        String hql = "FROM Room r";
        Query<Room> query = session.createQuery(hql, Room.class);
        List<Room> rooms = query.getResultList();
        for (Room room : query.list()) {
            log.info(room.toString());
        }
        return rooms;
    }

    // Метод для Добавления Номера в Базу Данных:
    public void createRoom(Room room) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Добавление Номера в Базу Данных: {}", room);
        session.persist(room);
        log.info("Номер Успешно Добавлен в Базу Данных: {}", room);
    }

    // Метод для Проверки Существования Номера по Номеру Размещения:
    public boolean doesRoomNumberExist(int roomNumber) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Проверка Существования Номера: {} в Базе Данных.", roomNumber);
        String hql = "SELECT COUNT(r) FROM Room r WHERE r.roomNumber = :roomNumber";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("roomNumber", roomNumber);
        Long count = query.uniqueResult();
        return count != null && count > 0;
    }

    // Метод для Получения Номера по Номеру Размещения:
    public Room findRoomByNumber(int roomNumber) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Получение Номера по Номеру Размещения: {} в Базе Данных.", roomNumber);
        String hql = "FROM Room r WHERE r.roomNumber = :roomNumber";
        Query<Room> query = session.createQuery(hql, Room.class);
        query.setParameter("roomNumber", roomNumber);
        return query.uniqueResult();
    }

    // Метод для Изменения Цены Номера в Базе Данных:
    public void updateRoomPrice(Room room) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Обновление Цены Номера: {} в Базе Данных.", room.getRoomNumber());
        session.merge(room);
    }

    // Метод для Получения Списка всех Номеров Отсортированных по Параметру из Базы Данных:
    public List<Room> getSortedInformationOfRooms(String parameter) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Получения Списка всех Номеров Отсортированных по Параметру: {} из Базы Данных", parameter);
        String hql = "FROM Room r ORDER BY " + getOrderByClause(parameter);
        Query<Room> query = session.createQuery(hql, Room.class);
        return query.getResultList();
    }

    // Метод для Получения Списка Свободных Номеров Отсортированных по Параметру из Базы Данных:
    public List<Room> getSortedInformationOfAvailableRooms(String parameter) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Запрос на Получения Списка Свободных Номеров Отсортированных по Параметру: {} из Базы Данных", parameter);
        String hql = "FROM Room r WHERE r.roomStatus = 'NOT_INHABITED' ORDER BY " + getOrderByClause(parameter);
        Query<Room> query = session.createQuery(hql, Room.class);
        return query.getResultList();
    }

    // Метод для Обновления Статуса Номера в Базе Данных:
    public void updateRoomStatus(int roomNumber, RoomStatus status) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Обновление Статуса Номера: {} на {} в Базе Данных.", roomNumber, status);
        Room room = findRoomByNumber(roomNumber);
        if (room != null) {
            room.setRoomStatus(status);
            session.merge(room);
        } else {
            log.warn("Номер {} не Найден для Обновления Статуса.", roomNumber);
        }
    }

    // Метод для Получения Текущего Количества Жильцов в Номере:
    public long getCurrentOccupantsCount(int roomNumber) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Получение Текущего Количества Жителей в Номере: {} из Базы Данных.", roomNumber);
        String hql = "SELECT COUNT(t) FROM Tenant t WHERE t.room.roomNumber = :roomNumber";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("roomNumber", roomNumber);
        Long count = query.uniqueResult();
        return count != null ? count : 0;
    }

    // Метод для Получения Списка Номеров, которые будут Доступны по Определенной Дате из Базы Данных:
    public List<Room> roomsAvailableOnSpecificDate(LocalDate date) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Получение Списка Свободных Номеров по Дате: {} из Базы Данных.", date);
        String hql = "FROM Room r WHERE r.roomNumber NOT IN " +
                "(SELECT t.room.roomNumber FROM Tenant t WHERE :date BETWEEN t.checkInDate AND t.dateOfIssueOfTheRoom)";
        Query<Room> query = session.createQuery(hql, Room.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    // Метод для Получения Списка Истории Номера из Базы Данных:
    public List<RoomHistory> getRoomHistory(int roomNumber) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Получение Истории Номера: {}.", roomNumber);
        String hql = "FROM RoomHistory h WHERE h.roomNumber = :roomNumber ORDER BY h.historyID DESC";
        Query<RoomHistory> query = session.createQuery(hql, RoomHistory.class);
        query.setParameter("roomNumber", roomNumber);
        return query.getResultList();
    }

    // Метод для Получения Выражения Сортировки:
    private String getOrderByClause(String parameter) {
        return switch (parameter) {
            case "Цена" -> "r.roomPrice";
            case "Вместимость" -> "r.maxCapacity";
            case "Звезды" -> "r.countOfStars";
            default -> throw new IllegalArgumentException("Ошибка: Неверный Параметр Сортировки: " + parameter);
        };
    }
}