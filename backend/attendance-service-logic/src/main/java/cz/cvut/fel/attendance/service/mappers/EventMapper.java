package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.Event;
import cz.fel.cvut.attendance.service.model.EventDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "takenPlaces", expression = "java(eventEntity.getChildEventAttendances().size())")
    EventDto toDto(Event eventEntity);

    Event toEntity(EventDto eventDto);

    List<EventDto> toDtoList(List<Event> events);
    List<Event> toEntityList(List<EventDto> eventDtos);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEventFromDto(EventDto eventDto, @MappingTarget Event event);
}
