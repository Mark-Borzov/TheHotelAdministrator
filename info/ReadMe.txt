Перед Данными Действиями Необходимо запустить TomCat: ./startup.sh
Открыть Менеджер Приложений TomCat: http://localhost:8080/manager/html
Deploy Приложения в TomCat: mvn clean package tomcat7:deploy
Undeploy Приложения из TomCat: mvn tomcat7:undeploy
Deploy и Undeploy Приложения: mvn tomcat7:undeploy && mvn clean package tomcat7:deploy
Проверка Стилей: mvn checkstyle:check
Коллекция (Postman) для API: TheHotelAdministratorAPI.postman_collection.json
Логи Приложения: ~/ApacheTomcat-11.0.5/apache-tomcat-11.0.5/bin/the_hotel_administrator_logs Файл: application.log
Скрипт для Создания Базы Данных: info/setup_hotel_system.sh