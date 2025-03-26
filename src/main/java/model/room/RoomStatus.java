package model.room;

import lombok.Getter;

@Getter
public enum RoomStatus {

    NOT_INHABITED("Не Заселен"),
    INHABITED("Заселен"),
    FOR_REPAIR("На Ремонте");

    private final String description;

    RoomStatus(String description) {
        this.description = description;
    }
}