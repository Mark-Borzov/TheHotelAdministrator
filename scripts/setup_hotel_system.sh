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

# Заполнение Таблиц в Базе Данных:
sudo -u "$DB_USER" psql -d "$DB_NAME" -c "
INSERT INTO hotel (hotel_id, hotel_name) VALUES
('4c877fc9-a293-42a5-afc3-989afbf34a0f', 'Гостиница');
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

sudo -u "$DB_USER" psql -d "$DB_NAME" -c "
INSERT INTO rooms (room_id, room_number, hotel_id, room_type, room_status, room_price, max_capacity, count_of_stars, tenant_history_size) VALUES
('10600c02-7e08-445e-a730-300810592557', 1, '4c877fc9-a293-42a5-afc3-989afbf34a0f', 'STUDIO', 'INHABITED', 2560.00, 2, 2, 3),
('b68a3a08-51ef-4c00-8074-44df53fde0ad', 2, '4c877fc9-a293-42a5-afc3-989afbf34a0f', 'APARTMENT', 'NOT_INHABITED', 8750.00, 3, 3, 3),
('ea21bb9d-6dba-4519-835c-7bd2e5c36105', 3, '4c877fc9-a293-42a5-afc3-989afbf34a0f', 'LUX', 'NOT_INHABITED', 25600.00, 5, 5, 3);
"

sudo -u "$DB_USER" psql -d "$DB_NAME" -c "
INSERT INTO tenants (tenant_id, tenant_name, tenant_inn, check_in_date, date_of_issue_of_the_room, room_number, room_id) VALUES
('e77cf86b-978e-4ce2-bb51-b76815130262', 'Ира', '637177858851', '2025-02-20', '2025-02-26', 1, '10600c02-7e08-445e-a730-300810592557'),
('930ab55c-d909-4956-b518-3ff863d3cfa1', 'Кристина', '177923562175', '2025-02-20', '2025-02-26', 1, '10600c02-7e08-445e-a730-300810592557');
"

sudo -u "$DB_USER" psql -d "$DB_NAME" -c "
INSERT INTO services (service_id, tenant_id, service_name, service_price, service_date) VALUES
('3b2866c7-b0bd-4a24-b5df-bc63df7568d1', 'e77cf86b-978e-4ce2-bb51-b76815130262', 'Завтрак', 200.00, '2025-02-20'),
('ab3763ce-5994-48d8-9ae2-470908d5f40f', 'e77cf86b-978e-4ce2-bb51-b76815130262', 'Обед', 350.00, '2025-02-20'),
('0478af60-3b33-4dfd-9314-b5c90a42755f', 'e77cf86b-978e-4ce2-bb51-b76815130262', 'Ужин', 500.00, '2025-02-20'),
('7fb34800-f846-497f-803c-26469b4db7c8', '930ab55c-d909-4956-b518-3ff863d3cfa1', 'Завтрак', 200.00, '2025-02-20'),
('0e79af6c-5225-4bba-b5a3-1d16ff607e32', '930ab55c-d909-4956-b518-3ff863d3cfa1', 'Обед', 350.00, '2025-02-20'),
('c4ae5306-4856-46bb-a302-32f12dd87f17', '930ab55c-d909-4956-b518-3ff863d3cfa1', 'Ужин', 500.00, '2025-02-20');
"