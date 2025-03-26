package mapper;

import org.mapstruct.factory.Mappers;
import dto.room.CreateRoomRequestDTO;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import model.room.Room;

@Mapper
public interface RoomMapper {

    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);
    @Mapping(target = "roomID", expression = "java(model.IDGenerator.generateID())")
    @Mapping(target = "roomStatus", constant = "NOT_INHABITED")
    @Mapping(target = "roomType", source = "request.roomType")
    @Mapping(target = "roomNumber", source = "request.roomNumber")
    @Mapping(target = "roomPrice", source = "request.roomPrice")
    @Mapping(target = "maxCapacity", source = "request.maxCapacity")
    @Mapping(target = "countOfStars", source = "request.countOfStars")
    @Mapping(target = "tenantHistorySize", source = "tenantHistorySize")
    Room toRoom(CreateRoomRequestDTO request, boolean roomStatusChangeEnabled, int tenantHistorySize);
}