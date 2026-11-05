package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.Trainer;
import cz.cvut.fel.attendance.service.model.TrainingUnit;
import cz.fel.cvut.attendance.service.model.parent.ChildUpcomingTrainingUnitDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ChildUpcomingTrainingUnitMapper {
    @Mapping(source = "trainingSchedule.dayOfWeek", target = "dayOfWeek")
    @Mapping(source = "trainingSchedule.training.school.name", target = "schoolName")
    @Mapping(source = "trainingSchedule.startTime", target = "startTime")
    @Mapping(source = "trainingSchedule.endTime", target = "endTime")
    @Mapping(source = "trainingSchedule.training.name", target = "name")
    @Mapping(source = "trainingSchedule.training.trainers", target = "trainerNames", qualifiedByName = "mapNames")
    @Mapping(source = "trainingSchedule.training.trainers", target = "trainerPhoneNumbers", qualifiedByName = "mapPhoneNumbers")
    ChildUpcomingTrainingUnitDto toDto(TrainingUnit trainingUnitEntity);

    default ChildUpcomingTrainingUnitDto toDtoWithChildren(TrainingUnit trainingUnit, String email) {
        List<String> childNames = trainingUnit.getTrainingSchedule().getTraining()
                .getChildren()
                .stream()
                .filter(child -> child.getParent() != null &&
                        email.equals(child.getParent().getEmail()))
                .map(child -> child.getFirstName() + " " + child.getLastName())
                .toList();

        ChildUpcomingTrainingUnitDto childUpcomingTrainingUnitDto = toDto(trainingUnit);

        return new ChildUpcomingTrainingUnitDto(
                childUpcomingTrainingUnitDto.id(),
                childUpcomingTrainingUnitDto.date(),
                childUpcomingTrainingUnitDto.dayOfWeek(),
                childUpcomingTrainingUnitDto.name(),
                childUpcomingTrainingUnitDto.schoolName(),
                childUpcomingTrainingUnitDto.startTime(),
                childUpcomingTrainingUnitDto.endTime(),
                childNames,
                childUpcomingTrainingUnitDto.trainerNames(),
                childUpcomingTrainingUnitDto.trainerPhoneNumbers()
        );
    }

    @Named("mapNames")
    default List<String> mapNames(List<Trainer> trainers) {
        return trainers.stream()
                .map(trainer -> trainer.getFirstName() + " " + trainer.getLastName())
                .collect(Collectors.toList());
    }


    @Named("mapPhoneNumbers")
    default List<String> mapPhoneNumbers(List<Trainer> trainers) {
        return trainers.stream()
                .map(Trainer::getPhoneNumber)
                .collect(Collectors.toList());
    }
}
