package cz.cvut.fel.attendance.service.api;

import cz.fel.cvut.attendance.service.api.SchoolApi;
import cz.fel.cvut.attendance.service.model.HolidayDto;
import cz.fel.cvut.attendance.service.model.SchoolDto;
import cz.cvut.fel.attendance.service.model.School;
import cz.cvut.fel.attendance.service.service.SchoolService;
//import cz.fel.cvut.attendance.service.api.SchoolApi;
import cz.fel.cvut.attendance.service.model.TrainingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SchoolApiImpl implements SchoolApi {

    private final SchoolService schoolService;

    @Override
    public ResponseEntity<SchoolDto> createSchool(SchoolDto schoolDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(schoolService.createSchool(schoolDto));
    }

    @Override
    public ResponseEntity<List<HolidayDto>> getHolidays(Long id) {
        return ResponseEntity.ok(schoolService.getHolidays(id));
    }

    @Override
    public ResponseEntity<HolidayDto> addHolidayBySchoolId(Long id, HolidayDto holidayDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(schoolService.addHolidayBySchoolId(id, holidayDto));
    }

    @Override
    public ResponseEntity<Void> addHoliday(HolidayDto holidayDto) {
        schoolService.addHoliday(holidayDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<SchoolDto> getSchool(Long id) {
        return ResponseEntity.ok(schoolService.getSchool(id));
    }

    @Override
    public ResponseEntity<List<TrainingDto>> getTrainingsBySchool(Long id) {
        return ResponseEntity.ok(schoolService.getTrainingsBySchool(id));
    }

    @Override
    public ResponseEntity<List<SchoolDto>> getSchools() {
        return ResponseEntity.ok(schoolService.getSchools());
    }

    @Override
    public ResponseEntity<Void> deleteSchool(Long id) {
        schoolService.deleteSchool(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<SchoolDto> updateSchool(Long id, SchoolDto schoolDto) {
        return ResponseEntity.ok(schoolService.updateSchool(id, schoolDto));
    }
}
