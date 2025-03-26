package config;

import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.context.annotation.PropertySource;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.SessionFactory;
import javax.sql.DataSource;
import java.io.IOException;

@EnableWebMvc
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {"config", "controller", "dao", "exception",
                                "model", "service", "mapper"})
public class WebAppConfig {

    // Метод для Настройки Валидации:
    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    // Метод для Валидации Параметров В Контроллерах:
    @Bean
    public static MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    // Метод для Настройки "LocalDate":
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    // Метод Создания Объекта для Подключения к Базе Данных:
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/hotel_system");
        dataSource.setUsername("postgres");
        dataSource.setPassword("12345");
        return dataSource;
    }

    // Метод Создания Объекта для Управления Транзакциями:
    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);
        return txManager;
    }

    // Метод Создания Объекта для Управления Сессиями Hibernate:
    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) throws IOException {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("model");
        sessionFactory.getHibernateProperties().put("hibernate.hbm2ddl.auto", "update");
        sessionFactory.afterPropertiesSet();
        return sessionFactory.getObject();
    }
}