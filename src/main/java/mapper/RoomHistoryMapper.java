package mapper;

import org.mapstruct.factory.Mappers;
import dto.room.RoomHistoryDTO;
import model.room.RoomHistory;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper
public interface RoomHistoryMapper {

    RoomHistoryMapper INSTANCE = Mappers.getMapper(RoomHistoryMapper.class);
    @Mapping(target = "message", constant = "Запись Истории Номера:")
    @Mapping(target = "roomID", source = "roomHistory.roomID")
    @Mapping(target = "roomNumber", source = "roomHistory.roomNumber")
    @Mapping(target = "tenantName", source = "roomHistory.tenantName")
    @Mapping(target = "tenantINN", source = "roomHistory.tenantInn")
    @Mapping(target = "checkInDate", source = "roomHistory.checkInDate")
    @Mapping(target = "dateOfIssueOfTheRoom", source = "roomHistory.dateOfIssueOfTheRoom")
    RoomHistoryDTO toRoomHistoryDTO(RoomHistory roomHistory);
}