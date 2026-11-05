package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.Child;
import cz.fel.cvut.attendance.service.model.ChildDto;
import cz.fel.cvut.attendance.service.model.admin.ChildParentInfo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChildMapper {
    @Mapping(source = "training.id", target = "trainingId")
    @Mapping(source = "training.name", target = "trainingName")
    ChildDto toDto(Child childEntity);

    Child toEntity(ChildDto childDto);

    List<ChildDto> toDtoList(List<Child> children);

    List<Child> toEntityList(List<ChildDto> childDtos);

    @Mapping(source = "training.school.name", target = "schoolName")
    @Mapping(target = "parentName", expression = "java(childEntity.getParent().getFirstName() + \" \" + childEntity.getParent().getLastName())")
    @Mapping(target = "email", expression = "java(childEntity.getParent().getEmail())")
    @Mapping(target = "phoneNumber", expression = "java(childEntity.getParent().getPhoneNumber())")
    ChildParentInfo childParentInfoToDto(Child childEntity);

    List<ChildParentInfo> childParentInfoToDtoList(List<Child> children);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateChildFromDto(ChildDto childDto, @MappingTarget Child Child);
}
