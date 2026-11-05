package cz.cvut.fel.attendance.service.api;

import cz.cvut.fel.attendance.service.service.AppSettingsService;
import cz.fel.cvut.attendance.service.api.AppSettingsApi;
import cz.fel.cvut.attendance.service.model.admin.AppSettingsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AppSettingsApiImpl implements AppSettingsApi {

    private final AppSettingsService appSettingsService;

    @Override
    public ResponseEntity<AppSettingsDto> getSettings() {
        return ResponseEntity.ok(appSettingsService.getAppSettings());
    }

    @Override
    public ResponseEntity<AppSettingsDto> toggleEnrollment() {
        return ResponseEntity.ok(appSettingsService.toggleEnrollment());
    }

    @Override
    public ResponseEntity<AppSettingsDto> toggleTrainingGeneration() {
        return ResponseEntity.ok(appSettingsService.toggleTrainingGeneration());
    }
}
