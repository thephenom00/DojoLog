package cz.cvut.fel.attendance.service.api;

import cz.cvut.fel.attendance.service.service.EventService;
import cz.fel.cvut.attendance.service.api.EventApi;
import cz.fel.cvut.attendance.service.model.EventDto;
import cz.fel.cvut.attendance.service.model.TrainerDto;
import cz.fel.cvut.attendance.service.model.trainer.RegisteredChildInEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventApiImpl implements EventApi {

    private final EventService eventService;

    @Override
    public ResponseEntity<EventDto> createEvent(EventDto eventDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(eventDto));
    }

    @Override
    public ResponseEntity<EventDto> getEvent(Long id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    @Override
    public ResponseEntity<RegisteredChildInEventDto> registerChild(Long id, Long childId, String description) {
        return ResponseEntity.ok(eventService.registerChild(id, childId, description));
    }

    @Override
    public ResponseEntity<TrainerDto> addTrainer(Long id, Long trainerId) {
        return ResponseEntity.ok(eventService.addTrainer(id, trainerId));
    }

    @Override
    public ResponseEntity<Void> removeTrainer(Long id, Long trainerId) {
        eventService.removeTrainer(id, trainerId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<EventDto>> getEvents() {
        return ResponseEntity.ok(eventService.getEvents());
    }

    @Override
    public ResponseEntity<Void> deleteEvent(Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<EventDto> updateEvent(Long id, EventDto eventDto) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventDto));
    }

    @Override
    public ResponseEntity<List<RegisteredChildInEventDto>> getRegisteredChildren(Long id) {
        return ResponseEntity.ok(eventService.getRegisteredChildren(id));
    }

    @Override
    public ResponseEntity<List<TrainerDto>> getRegisteredTrainers(Long id) {
        return ResponseEntity.ok(eventService.getRegisteredTrainers(id));
    }
}
