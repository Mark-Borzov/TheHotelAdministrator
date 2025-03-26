package model;

import java.util.HashSet;
import java.util.UUID;
import java.util.Set;

public class IDGenerator {

    // Хранилище Уникальных ID для Сущностей:
    private static final Set<UUID> generatedIDs = new HashSet<>();

    // Метод для Получения Уникального ID для Создаваемой Сущности:
    public static UUID generateID() {
        UUID newID = UUID.randomUUID();
        while (generatedIDs.contains(newID)) {
            newID = UUID.randomUUID();
        }
        generatedIDs.add(newID);
        return newID;
    }
}