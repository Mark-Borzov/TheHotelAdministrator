Перед Данными Действиями Необходимо запустить TomCat: ./startup.sh
Открыть Менеджер Приложений TomCat: http://localhost:8080/manager/html
Deploy Приложения в TomCat: mvn clean package tomcat7:deploy
Undeploy Приложения из TomCat: mvn tomcat7:undeploy
Deploy и Undeploy Приложения: mvn tomcat7:undeploy && mvn clean package tomcat7:deploy
Проверка Стилей: mvn checkstyle:check
Запуск Тестов: mvn clean test
Коллекция (Postman) для API: TheHotelAdministratorAPI.postman_collection.json
Скрипт для Создания Базы Данных: info/setup_hotel_system.sh