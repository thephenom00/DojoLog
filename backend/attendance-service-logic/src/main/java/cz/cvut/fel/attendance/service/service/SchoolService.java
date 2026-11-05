package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.HolidayMapper;
import cz.cvut.fel.attendance.service.mappers.TrainingMapper;
import cz.cvut.fel.attendance.service.model.Holiday;
import cz.cvut.fel.attendance.service.model.Training;
import cz.cvut.fel.attendance.service.repository.HolidayRepository;
import cz.cvut.fel.attendance.service.repository.TrainingRepository;
import cz.fel.cvut.attendance.service.exception.SchoolException;
import cz.fel.cvut.attendance.service.model.HolidayDto;
import cz.fel.cvut.attendance.service.model.SchoolDto;
import cz.cvut.fel.attendance.service.mappers.SchoolMapper;
import cz.cvut.fel.attendance.service.model.School;
import cz.cvut.fel.attendance.service.repository.SchoolRepository;
import cz.fel.cvut.attendance.service.model.TrainingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final TrainingRepository trainingRepository;

    private final SchoolMapper schoolMapper;

    private final TrainingMapper trainingMapper;

    private final HolidayMapper holidayMapper;

    private final HolidayRepository holidayRepository;

    public List<HolidayDto> getHolidays(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new SchoolException("School with ID: " + id + " is not existing.",
                        HttpStatus.NOT_FOUND));

        return holidayMapper.toDtoList(school.getHolidays());
    }


    @Transactional
    public HolidayDto addHoliday(Long id, HolidayDto holidayDto) {
        List<School> schools = id == null ? schoolRepository.findAll() :
                List.of(schoolRepository.findById(id).orElseThrow(() -> new SchoolException("School with ID: " + id + " was not found.", HttpStatus.NOT_FOUND)));
        Holiday holiday;

        for (School school : schools) {
            holiday = holidayMapper.toEntity(holidayDto);
            holiday.setSchool(school);

            holidayRepository.save(holiday);
            school.getHolidays().add(holiday);
            schoolRepository.save(school);
        }

        return holidayDto;
    }

    @Transactional
    public SchoolDto createSchool(SchoolDto schoolDto) {
        School school = schoolMapper.toEntity(schoolDto);

        String trimmedName = schoolDto.name() != null ? schoolDto.name().trim() : null;
        if (trimmedName != null && schoolRepository.existsByName(trimmedName)) {
            throw new SchoolException("School with name '" + trimmedName + "' already exists.",
                    HttpStatus.CONFLICT);
        }

        schoolRepository.save(school);
        return schoolMapper.toDto(school);
    }

    @Transactional
    public void deleteSchool(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new SchoolException("School with ID: " + id + " is not existing.",
                        HttpStatus.NOT_FOUND));

        for (Training training : school.getTrainings()) {
            training.setSchool(null);
        }

        schoolRepository.delete(school);
    }

    public SchoolDto getSchool(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new SchoolException("School with ID " + id + " not found.",
                        HttpStatus.NOT_FOUND));
        return schoolMapper.toDto(school);
    }

    public List<SchoolDto> getSchools() {
        List<School> schools = schoolRepository.findAll();
        return schoolMapper.toDtoList(schools);
    }

    @Transactional
    public SchoolDto updateSchool(Long id, SchoolDto schoolDto) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new SchoolException("School with ID " + id + " not found.",
                        HttpStatus.NOT_FOUND));

        schoolMapper.updateSchoolFromDto(schoolDto, school);

        schoolRepository.save(school);
        return schoolMapper.toDto(school);
    }

    public List<TrainingDto> getTrainingsBySchool(Long id) {
        schoolRepository.findById(id)
                .orElseThrow(() -> new SchoolException("School with ID " + id + " not found.",
                        HttpStatus.NOT_FOUND));

        List<Training> trainings = trainingRepository.findActiveBySchoolId(id);

        return trainingMapper.toDtoList(trainings);
    }
}
