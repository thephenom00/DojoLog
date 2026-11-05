package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.model.AppSettings;
import cz.cvut.fel.attendance.service.repository.AppSettingsRepository;
import cz.cvut.fel.attendance.service.repository.ChildRepository;
import cz.cvut.fel.attendance.service.repository.TrainingRepository;
import cz.fel.cvut.attendance.service.model.admin.AppSettingsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppSettingsService {

    private final AppSettingsRepository appSettingsRepository;
    private final ChildRepository childRepository;
    private final TrainingRepository trainingRepository;

    private AppSettings getSettings() {
        return appSettingsRepository.findById(1L)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "App settings not found."));
    }

    public AppSettingsDto getAppSettings() {
        AppSettings settings = getSettings();
        return new AppSettingsDto(settings.isEnrollmentOpen(), settings.isTrainingGeneration());
    }

    @Transactional
    public AppSettingsDto toggleEnrollment() {
        AppSettings settings = getSettings();
        settings.setEnrollmentOpen(!settings.isEnrollmentOpen());
        appSettingsRepository.save(settings);

        if (settings.isEnrollmentOpen()) {
            childRepository.clearTrainings();
            trainingRepository.setInitialCapacity();
        }
        return new AppSettingsDto(settings.isEnrollmentOpen(), settings.isTrainingGeneration());
    }

    @Transactional
    public AppSettingsDto toggleTrainingGeneration() {
        AppSettings settings = getSettings();
        settings.setTrainingGeneration(!settings.isTrainingGeneration());
        appSettingsRepository.save(settings);
        return new AppSettingsDto(settings.isEnrollmentOpen(), settings.isTrainingGeneration());
    }
}
