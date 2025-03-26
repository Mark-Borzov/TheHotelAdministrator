#!/bin/bash

# Перед Запуском Скрипта Необходимо Установить Права на Выполнение:
# Ввести в Терминале ---> chmod +x setup_hotel_system.sh
# Запуск Скрипта ---> bash ./setup_hotel_system.sh

DB_USER="postgres" 
DB_NAME="hotel_system"

# Проверка Существования Базы Данных:
if sudo -u "$DB_USER" psql -lqt | cut -d \| -f 1 | grep -qw "$DB_NAME"; then
    echo "База Данных '$DB_NAME' уже Существует."
else
    # Создание Базы Данных:
    sudo -u "$DB_USER" psql -c "CREATE DATABASE $DB_NAME;"

    # Проверка Успешности Выполнения Операции:
    if [ $? -eq 0 ]; then
        echo "База Данных '$DB_NAME' Успешно Создана."
    else
        echo "Ошибка при Создании Базы Данных '$DB_NAME'."
    fi
fi

# Создание Таблиц в Базе Данных:
sudo -u "$DB_USER" psql -d "$DB_NAME" -c "
CREATE TABLE hotel (
    hotel_id UUID PRIMARY KEY NOT NULL,
    hotel_name VARCHAR(50) NOT NULL
);
"

sudo -u "$DB_USER" psql -d "$DB_NAME" -c "
CREATE TABLE services_provided (
    service_name VARCHAR(50) PRIMARY KEY NOT NULL,
    service_price DECIMAL(10, 2) NOT NULL
);
"

sudo -u "$DB_USER" psql -d "$DB_NAME" -c "
CREATE TABLE rooms (
    room_id UUID PRIMARY KEY NOT NULL,
    room_number INT NOT NULL UNIQUE,
    hotel_id UUID NOT NULL,
    room_type VARCHAR(50) NOT NULL,
    room_status VARCHAR(50) NOT NULL,
    room_price DECIMAL(10, 2) NOT NULL,
    max_capacity INT NOT NULL,
    count_of_stars INT NOT NULL,
    tenant_history_size INT DEFAULT 0,
    FOREIGN KEY (hotel_id) REFERENCES hotel (hotel_id)
);
"

sudo -u "$DB_USER" psql -d "$DB_NAME" -c "
CREATE TABLE tenants (
    tenant_id UUID PRIMARY KEY NOT NULL,
    tenant_name VARCHAR(50) NOT NULL,
    tenant_inn VARCHAR(50) NOT NULL,
    check_in_date DATE NOT NULL,
    date_of_issue_of_the_room DATE NOT NULL,
    room_number INT NOT NULL,
    room_id UUID NOT NULL,
    FOREIGN KEY (room_id) REFERENCES rooms (room_id)
);
"

sudo -u "$DB_USER" psql -d "$DB_NAME" -c "
CREATE TABLE services (
    service_id UUID PRIMARY KEY NOT NULL,
    tenant_id UUID NOT NULL,
    service_name VARCHAR(50) NOT NULL,
    service_price DECIMAL(10, 2) NOT NULL,
    service_date DATE NOT NULL,
    FOREIGN KEY (service_name) REFERENCES services_provided (service_name),
    FOREIGN KEY (tenant_id) REFERENCES tenants (tenant_id)
);
"

sudo -u "$DB_USER" psql -d "$DB_NAME" -c "
CREATE TABLE room_history (
    history_id SERIAL PRIMARY KEY NOT NULL,
    room_id UUID NOT NULL,
    room_number INT NOT NULL,
    tenant_name VARCHAR(50) NOT NULL,
    tenant_inn VARCHAR(50) NOT NULL,
    check_in_date DATE NOT NULL,
    date_of_issue_of_the_room DATE NOT NULL,
    FOREIGN KEY (room_id) REFERENCES rooms (room_id)
);
"

sudo -u "$DB_USER" psql -d "$DB_NAME" -c "
INSERT INTO services_provided (service_name, service_price) VALUES
('Завтрак', 200.00),
('Обед', 350.00),
('Ужин', 500.00),
('Сауна', 600.00),
('Фитнес-центр', 1500.00),
('Wi-Fi', 100.00);
"