package cz.fel.cvut.attendance.service.api;

import cz.fel.cvut.attendance.service.model.ChildDto;
import cz.fel.cvut.attendance.service.model.parent.ChildEventStatusDto;
import cz.fel.cvut.attendance.service.model.parent.ChildUpcomingTrainingUnitDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RequestMapping("/parent")
public interface ParentApi {
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value="/{email}/training-unit/upcoming")
    ResponseEntity<List<ChildUpcomingTrainingUnitDto>> getUpcomingTrainingUnits(@PathVariable String email);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value="/{email}/children")
    ResponseEntity<List<ChildDto>> getChildren(@PathVariable String email);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value="/{email}/children/event-status/{id}")
    ResponseEntity<List<ChildEventStatusDto>> getChildrenEventStatus(@PathVariable String email, @PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value="/{email}/create-child", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ChildDto> createChild(@PathVariable String email, @RequestBody ChildDto childDto);
}
