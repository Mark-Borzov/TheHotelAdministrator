package dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import exception.tenant.TenantSettleErrorException;
import org.springframework.stereotype.Repository;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import lombok.extern.slf4j.Slf4j;
import model.room.RoomHistory;
import org.hibernate.Session;
import model.tenant.Tenant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
public class TenantDAO {

    // Параметр для Установки Количества записей о Жителях в Истории Номера:
    @Value("${Room.tenantHistorySize}")
    private int tenantHistorySize;

    private final SessionFactory sessionFactory;

    @Autowired
    public TenantDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Метод для Добавления Жителя в Номер Базы Данных:
    public void addTenant(Tenant tenant) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Добавление Жителя в Базу Данных: {}", tenant);
        String hql = "SELECT r.maxCapacity FROM Room r WHERE r.roomNumber = :roomNumber";
        Query<Integer> query = session.createQuery(hql, Integer.class);
        query.setParameter("roomNumber", tenant.getRoom().getRoomNumber());
        Integer maxCapacity = query.uniqueResult();
        String hqlCurrentOccupants = "SELECT COUNT(t) FROM Tenant t WHERE t.room.roomNumber = :roomNumber";
        Query<Long> queryCurrentOccupants = session.createQuery(hqlCurrentOccupants, Long.class);
        queryCurrentOccupants.setParameter("roomNumber", tenant.getRoom().getRoomNumber());
        Long currentOccupants = queryCurrentOccupants.uniqueResult();
        if (currentOccupants != null && currentOccupants >= maxCapacity) {
            throw new TenantSettleErrorException("Ошибка: Нельзя добавить жильца. Номер "
                    + tenant.getRoom().getRoomNumber() + " Полностью Заселен.");
        } else {
            this.removeOldestTenantIfNecessary(tenant.getRoom().getRoomID());
            session.persist(tenant);
        }
    }

    // Метод для Удаления Жителя из Базы Данных:
    public void removeTenant(Tenant tenant) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Удаление Жителя из Базы Данных: {}", tenant);
        RoomHistory roomHistory = new RoomHistory();
        roomHistory.setRoomID(tenant.getRoom().getRoomID());
        roomHistory.setRoomNumber(tenant.getRoom().getRoomNumber());
        roomHistory.setTenantName(tenant.getTenantName());
        roomHistory.setTenantInn(tenant.getTenantINN());
        roomHistory.setCheckInDate(tenant.getCheckInDate());
        roomHistory.setDateOfIssueOfTheRoom(LocalDate.now());
        session.persist(roomHistory);
        String hql = "DELETE FROM Service s WHERE s.tenant.id = :tenantId";
        session.createMutationQuery(hql).setParameter("tenantId", tenant.getTenantID()).executeUpdate();
        session.remove(tenant);
    }

    // Метод для Проверки Существования Жителя с Указанным ИНН в Базе Данных:
    public boolean existsByInn(String tenantINN) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Проверка Существования Жителя с Указанным ИНН: {} в Базе Данных", tenantINN);
        String hql = "SELECT COUNT(t) FROM Tenant t WHERE t.tenantINN = :tenantINN";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("tenantINN", tenantINN);
        Long count = query.uniqueResult();
        return count != null && count > 0;
    }

    // Метод для Поиска Жителя по ИНН и Номеру Комнаты в Базе Данных:
    public Tenant findTenantByInnAndRoomNumber(String tenantINN, int roomNumber) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Поиск Жителя по ИНН: {} и Номеру Комнаты: {} в Базе Данных", tenantINN, roomNumber);
        String hql = "FROM Tenant t WHERE t.tenantINN = :tenantINN AND t.room.roomNumber = :roomNumber";
        Query<Tenant> query = session.createQuery(hql, Tenant.class);
        query.setParameter("tenantINN", tenantINN);
        query.setParameter("roomNumber", roomNumber);
        return query.uniqueResult();
    }

    // Метод для Проверки Наличия Жителей в Гостинице Базы Данных:
    public boolean availabilityOfTenant() {
        Session session = sessionFactory.getCurrentSession();
        log.info("Проверка Наличия Жителей в Гостинице.");
        String hql = "SELECT COUNT(t) FROM Tenant t";
        Query<Long> query = session.createQuery(hql, Long.class);
        Long count = query.uniqueResult();
        return count == null || count <= 0;
    }

    // Метод для Получения Списка Жителей, Отсортированных по Передаваемому Параметру из Базы Данных:
    public List<Tenant> getSortedTenants(String sortBy) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Получение Списка Жителей, Отсортированных по Параметру: {} из Базы Данных", sortBy);
        String hql = "FROM Tenant t ORDER BY " + this.getOrderByClause(sortBy);
        Query<Tenant> query = session.createQuery(hql, Tenant.class);
        return query.getResultList();
    }

    // Метод для Получения Общего Количества Жильцов из Базы Данных:
    public Long countAllTenants() {
        Session session = sessionFactory.getCurrentSession();
        log.info("Подсчет Всех Жителей Гостиницы из Базы Данных.");
        String hql = "SELECT COUNT(t) FROM Tenant t";
        Query<Long> query = session.createQuery(hql, Long.class);
        return query.uniqueResult();
    }

    // Метод для Поиска Жителя по ИНН в Базе Данных:
    public Tenant findTenantByInn(String tenantINN) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Поиск Жителя Гостиницы по ИНН: {} в Базе Данных", tenantINN);
        String hql = "FROM Tenant t WHERE t.tenantINN = :tenantINN";
        Query<Tenant> query = session.createQuery(hql, Tenant.class);
        query.setParameter("tenantINN", tenantINN);
        return query.uniqueResult();
    }

    // Метод для Получения Оплаты за Предоставленные Услуги из Базы Данных:
    public double getPaymentForTenant(Tenant tenant) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Получение Оплаты Предоставляемых Услуг для Жителя: {}.", tenant);
        String hql = "SELECT SUM(s.servicePrice) FROM Service s WHERE s.tenant.tenantID = :tenantId";
        Query<Double> query = session.createQuery(hql, Double.class);
        query.setParameter("tenantId", tenant.getTenantID());
        Double totalAmount = query.uniqueResult();
        return totalAmount != null ? totalAmount : 0.0;
    }

    // Метод для Удаления Самого Старого Жителя из Истории Номера Базы Данных:
    private void removeOldestTenantIfNecessary(UUID roomId) {
        int recordsInTheHistoryOfTenants = this.tenantHistorySize;
        String hqlCountTenants = "SELECT COUNT(h) FROM RoomHistory h WHERE h.roomID = :roomId";
        String hqlDeleteOldestTenant = "DELETE FROM RoomHistory h WHERE h.historyID = " +
                "(SELECT MIN(h2.historyID) FROM RoomHistory h2 WHERE h2.roomID = :roomId)";
        Long tenantCount = sessionFactory.getCurrentSession().createQuery(hqlCountTenants, Long.class)
                .setParameter("roomId", roomId)
                .uniqueResult();
        if (tenantCount != null && tenantCount >= recordsInTheHistoryOfTenants) {
            sessionFactory.getCurrentSession().createMutationQuery(hqlDeleteOldestTenant)
                    .setParameter("roomId", roomId)
                    .executeUpdate();
            log.info("Удален Самый Старый Житель из Истории для Номера с ID: {}.", roomId);
        }
    }

    // Метод для Получения Выражения Сортировки:
    private String getOrderByClause(String parameter) {
        return switch (parameter) {
            case "Алфавит" -> "t.tenantName";
            case "Освобождение" -> "t.dateOfIssueOfTheRoom";
            default -> throw new IllegalArgumentException("Ошибка: Неверный Параметр Сортировки: " + parameter);
        };
    }
}