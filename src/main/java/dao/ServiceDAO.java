package dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import model.service.ServicesProvided;
import org.hibernate.SessionFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Query;
import model.service.Service;
import org.hibernate.Session;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
public class ServiceDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public ServiceDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Метод Добавления Услуги для Жителя в Базе Данных:
    public void addServiceForTenant(Service service) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Сохранение Услуги для Жителя: {} в Базу Данных", service);
        session.persist(service);
    }

    // Метод для Получения Предоставляемой Услуги по Имени из Базы Данных:
    public ServicesProvided getServiceByName(String serviceName) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Получение Услуги по Имени: {} из Базы Данных", serviceName);
        String hql = "FROM ServicesProvided s WHERE s.serviceServicesProvidedName = :serviceName";
        Query<ServicesProvided> query = session.createQuery(hql, ServicesProvided.class);
        query.setParameter("serviceName", serviceName);
        return query.uniqueResult();
    }

    // Метод Получения Списка Предоставляемых Услуг из Базы Данных:
    public List<ServicesProvided> getServicesProvidedSortedByPrice() {
        Session session = sessionFactory.getCurrentSession();
        log.info("Получение Списка Предоставляемых Услуг, Отсортированных по Цене из Базы Данных");
        String hql = "FROM ServicesProvided s ORDER BY s.serviceServicesProvidedPrice";
        Query<ServicesProvided> query = session.createQuery(hql, ServicesProvided.class);
        return query.getResultList();
    }

    // Метод для Получения Списка Услуг Жителя, Отсортированных по Передаваемому Параметру из Базы Данных:
    public List<Service> getSortedServicesForTenant(UUID tenantId, String sortBy) {
        Session session = sessionFactory.getCurrentSession();
        log.info("Получение Списка Услуг для Жителя с ID {}, Отсортированных по параметру: {} из Базы Данных",
                tenantId, sortBy);
        String hql = "FROM Service s WHERE s.tenant.tenantID = :tenantId ORDER BY " + this.getOrderByClause(sortBy);
        Query<Service> query = session.createQuery(hql, Service.class);
        query.setParameter("tenantId", tenantId);
        return query.getResultList();
    }

    // Метод для Получения Выражения Сортировки:
    private String getOrderByClause(String parameter) {
        return switch (parameter) {
            case "Цена" -> "s.servicePrice";
            case "Дата" -> "s.serviceDate";
            default -> throw new IllegalArgumentException("Ошибка: Неверный Параметр Сортировки: " + parameter);
        };
    }
}