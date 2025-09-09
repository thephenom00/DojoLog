package cz.fel.cvut.attendance.service.api;

import cz.fel.cvut.attendance.service.model.ChildDto;
import cz.fel.cvut.attendance.service.model.ParentDto;
import cz.fel.cvut.attendance.service.model.admin.RegistrationRequestDto;
import cz.fel.cvut.attendance.service.model.parent.ChildAttendanceDetailDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RequestMapping("/child")
public interface ChildApi {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/unassigned-children")
    ResponseEntity<List<RegistrationRequestDto>> getUnassignedChildren();

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/add-to-training")
    ResponseEntity<ChildDto> addChildToTraining(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{childId}/remove-from-training/{trainingId}")
    ResponseEntity<Void> removeChildFromTraining(@PathVariable Long childId, @PathVariable Long trainingId);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}/attendance-detail")
    ResponseEntity<List<ChildAttendanceDetailDto>> getChildAttendanceDetails(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}")
    ResponseEntity<ChildDto> getChild(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    ResponseEntity<List<ChildDto>> getChildren();

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{id}/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> deleteChild(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/update")
    ResponseEntity<ChildDto> updateChild(@PathVariable Long id, @RequestBody ChildDto childDto);

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/update-parent")
    ResponseEntity<ParentDto> updateChildParent(@PathVariable Long id, @RequestBody ParentDto parentDto);

}
