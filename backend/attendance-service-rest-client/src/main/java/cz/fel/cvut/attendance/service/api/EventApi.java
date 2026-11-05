package cz.fel.cvut.attendance.service.api;

import cz.fel.cvut.attendance.service.model.EventDto;
import cz.fel.cvut.attendance.service.model.PagedEventDto;
import cz.fel.cvut.attendance.service.model.TrainerDto;
import cz.fel.cvut.attendance.service.model.trainer.RegisteredChildInEventDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RequestMapping("/event")
public interface EventApi {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value="/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<EventDto> createEvent(@RequestBody EventDto eventDto);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value="/{id}")
    ResponseEntity<EventDto> getEvent(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value="/{id}/register-child/{childId}")
    ResponseEntity<RegisteredChildInEventDto> registerChild(@PathVariable Long id, @PathVariable Long childId, @RequestBody(required = false) String description);

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value="/{id}/remove-child/{childId}")
    ResponseEntity<Void> removeChild(@PathVariable Long id, @PathVariable Long childId);

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value="/{id}/add-trainer/{trainerId}")
    ResponseEntity<TrainerDto> addTrainer(@PathVariable Long id, @PathVariable Long trainerId);

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value="/{id}/remove-trainer/{trainerId}")
    ResponseEntity<Void> removeTrainer(@PathVariable Long id, @PathVariable Long trainerId);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/upcoming")
    ResponseEntity<PagedEventDto> getUpcomingEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    );

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/past")
    ResponseEntity<PagedEventDto> getPastEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    );

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> deleteEvent(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value="/{id}")
    ResponseEntity<EventDto> updateEvent(@PathVariable Long id, @RequestBody EventDto eventDto);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value="/{id}/registered-children")
    ResponseEntity<List<RegisteredChildInEventDto>> getRegisteredChildren(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value="/{id}/registered-trainers")
    ResponseEntity<List<TrainerDto>> getRegisteredTrainers(@PathVariable Long id);
}
