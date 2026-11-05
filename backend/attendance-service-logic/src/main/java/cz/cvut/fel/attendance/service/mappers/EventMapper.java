package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.enums.ReportCategory;
import cz.cvut.fel.attendance.service.model.Event;
import cz.cvut.fel.attendance.service.model.Trainer;
import cz.fel.cvut.attendance.service.model.EventDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "takenPlaces", expression = "java(eventEntity.getChildEventAttendances().size())")
    @Mapping(target = "trainerEmails", source = "trainers", qualifiedByName = "trainersToEmails")
    @Mapping(target = "trainerNames", source = "trainers", qualifiedByName = "trainersToNames")
    @Mapping(target = "trainerPhones", source = "trainers", qualifiedByName = "trainersToPhones")
    @Mapping(target = "eventType", source = "eventType", qualifiedByName = "categoryToCzechName")
    EventDto toDto(Event eventEntity);

    @Mapping(target = "trainerEmails", source = "trainers", qualifiedByName = "trainersToEmails")
    @Mapping(target = "trainerNames", source = "trainers", qualifiedByName = "trainersToNames")
    @Mapping(target = "trainerPhones", source = "trainers", qualifiedByName = "trainersToPhones")
    @Mapping(target = "takenPlaces", expression = "java(eventEntity.getChildEventAttendances().size())")
    @Mapping(target = "eventType", source = "eventType", qualifiedByName = "categoryToCzechName")
    List<EventDto> toDtoList(List<Event> events);

    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "eventType", source = "eventType", qualifiedByName = "czechNameToCategory")
    Event toEntity(EventDto eventDto);

    List<Event> toEntityList(List<EventDto> eventDtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "eventType", source = "eventType", qualifiedByName = "czechNameToCategory")
    void updateEventFromDto(EventDto eventDto, @MappingTarget Event event);

    @Named("trainersToEmails")
    default List<String> trainersToEmails(List<Trainer> trainers) {
        if (trainers == null) return List.of();
        return trainers.stream().map(Trainer::getEmail).collect(Collectors.toList());
    }

    @Named("trainersToNames")
    default List<String> trainersToNames(List<Trainer> trainers) {
        if (trainers == null) return List.of();
        return trainers.stream().map(t->t.getFirstName() + " " + t.getLastName()).collect(Collectors.toList());
    }

    @Named("trainersToPhones")
    default List<String> trainersToPhones(List<Trainer> trainers) {
        if (trainers == null) return List.of();
        return trainers.stream().map(Trainer::getPhoneNumber).collect(Collectors.toList());
    }

    @Named("czechNameToCategory")
    default ReportCategory czechNameToCategory(String czechName) {
        return ReportCategory.fromCzechName(czechName);
    }

    @Named("categoryToCzechName")
    default String categoryToCzechName(ReportCategory category) {
        return category != null ? category.getName() : null;
    }
}
