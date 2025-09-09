package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.ChildEventAttendance;
import cz.fel.cvut.attendance.service.model.trainer.RegisteredChildInEventDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChildEventAttendanceMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", expression = "java(childEventAttendance.getChild().getFirstName() + \" \" + childEventAttendance.getChild().getLastName())")
    @Mapping(target = "note", source = "note")
    @Mapping(target = "paymentReceived", source = "paymentReceived")
    @Mapping(target = "phoneNumber", source = "child.parent.phoneNumber")
    @Mapping(target = "email", source = "child.parent.email")
    @Mapping(target = "present", source = "present")
    RegisteredChildInEventDto toDto(ChildEventAttendance childEventAttendance);

    List<RegisteredChildInEventDto> toDtoList(List<ChildEventAttendance> childEventAttendances);
}
