package cz.fel.cvut.attendance.service.api;

import cz.fel.cvut.attendance.service.model.admin.AppSettingsDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/app/settings")
public interface AppSettingsApi {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    ResponseEntity<AppSettingsDto> getSettings();

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/enrollment")
    ResponseEntity<AppSettingsDto> toggleEnrollment();

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/generation")
    ResponseEntity<AppSettingsDto> toggleTrainingGeneration();
}
