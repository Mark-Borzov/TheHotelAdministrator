package model.room;

import lombok.Getter;

@Getter
public enum RoomType {

    STUDIO("Студия"),
    APARTMENT("Апартаменты"),
    LUX("Люкс");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }
}