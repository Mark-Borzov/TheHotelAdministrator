# TheHotelAdministrator

## Описание

Данный проект представляет собой REST API приложение администрирования гостиницы, предоставляя функционал для управления гостиницей, гостиничными номерами, жителями и услугами. Приложение разработано на языке Java с использованием фреймворка Spring MVC.

## Требования 

- Операционная система: `Linux`
- Java Development Kit (JDK): `17 и выше`
- Apache Maven: `3.6.0 и выше`
- Сервер приложений: `Apache Tomcat 7 и выше`
- Система управления базами данных: `PostgreSQL 12 и выше`
- Сервис для работы с API: `Postman`

## Функциональные Возможности

Запросы к API и их описания можно найти в файле: `/info/TheHotelAdministratorAPI.postman_collection.json`. Данный файл содержит все необходимые эндпоинты и параметры для взаимодействия с приложением.
Логи приложения находятся в файле по следующему пути: `/logs/application.log`

## Установка и Запуск
- Клонировать репозиторий: `git@github.com:Mark-Borzov/TheHotelAdministrator.git`
- Запустить скрипт для установки Базы Данных: `/scripts/setup_hotel_system.sh`
- Выполнить команду для сборки проекта: `mvn clean install`
- Скопировать war-файл: `/target/TheHotelAdministrator/war` в директорию: `webapps` сервера `Apache Tomcat`
- Запустить сервер Tomcat: `/[apache-tomcat]/bin/startup.sh`
- Открыть менеджер приложений `TomCat`: `http://localhost:8080/manager/html` и убедиться, что приложение запущено
- Импортировать файл: `/info/TheHotelAdministratorAPI.postman_collection.json` в `Postman` для взаимодействия с приложением