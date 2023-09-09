--liquibase formatted sql

--changeset skanash:1
--name: Create student name index
CREATE TABLE notification_task (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    message_text VARCHAR(255) NOT NULL,
    scheduled_time TIMESTAMP NOT NULL
);
