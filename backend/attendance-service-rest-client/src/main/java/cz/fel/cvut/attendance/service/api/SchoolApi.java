package cz.fel.cvut.attendance.service.api;

import cz.fel.cvut.attendance.service.model.HolidayDto;
import cz.fel.cvut.attendance.service.model.SchoolDto;
import cz.fel.cvut.attendance.service.model.TrainingDto;
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
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RequestMapping("/school")
public interface SchoolApi {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value="/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SchoolDto> createSchool(@RequestBody SchoolDto schoolDto);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value="/{id}/holidays", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<HolidayDto>> getHolidays(@PathVariable Long id);

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value="/{id}/add-holiday", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<HolidayDto> addHolidayBySchoolId(@PathVariable Long id, @RequestBody HolidayDto holidayDto);

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value="/add-holiday", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> addHoliday(@RequestBody HolidayDto holidayDto);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value="/{id}")
    ResponseEntity<SchoolDto> getSchool(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value="/{id}/trainings")
    ResponseEntity<List<TrainingDto>> getTrainingsBySchool(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    ResponseEntity<List<SchoolDto>> getSchools();

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> deleteSchool(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value="/{id}")
    ResponseEntity<SchoolDto> updateSchool(@PathVariable Long id, @RequestBody SchoolDto schoolDto);
}
