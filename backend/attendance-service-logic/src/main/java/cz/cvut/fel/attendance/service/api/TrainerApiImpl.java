package cz.cvut.fel.attendance.service.api;

import cz.cvut.fel.attendance.service.service.TrainerService;
import cz.fel.cvut.attendance.service.api.TrainerApi;
import cz.fel.cvut.attendance.service.model.PagedTrainingUnitDto;
import cz.fel.cvut.attendance.service.model.TrainerDto;
import cz.fel.cvut.attendance.service.model.trainer.CreateReportItemDto;
import cz.fel.cvut.attendance.service.model.trainer.TrainerMonthlyReportDto;
import cz.fel.cvut.attendance.service.model.trainer.TrainerReportDto;
import cz.fel.cvut.attendance.service.model.trainer.UpdateReportItemMoneyDto;
import cz.fel.cvut.attendance.service.model.trainer.UpdateTrainerReportNoteDto;
import cz.fel.cvut.attendance.service.model.TrainingUnitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TrainerApiImpl implements TrainerApi {

    private final TrainerService trainerService;

    @Override
    public ResponseEntity<List<TrainerDto>> getTrainers() {
        return ResponseEntity.ok(trainerService.getTrainers());
    }

    @Override
    public ResponseEntity<TrainerDto> updateTrainer(Long id, TrainerDto trainerDto) {
        return ResponseEntity.ok(trainerService.updateTrainer(id, trainerDto));
    }

    @Override
    public ResponseEntity<Void> deleteTrainer(Long id) {
        trainerService.deactivateTrainer(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<TrainingUnitDto>> getUpcomingTrainingUnits(String email) {
        return ResponseEntity.ok(trainerService.getUpcomingTrainingUnits(email));
    }

    @Override
    public ResponseEntity<PagedTrainingUnitDto> getPastTrainingUnits(String email, int page, int size) {
        return ResponseEntity.ok(trainerService.getPastTrainingUnits(email, page, size));
    }

    @Override
    public ResponseEntity<List<TrainerReportDto>> getCurrentMonthReport(String email) {
        return ResponseEntity.ok(trainerService.getCurrentMonthReport(email));
    }

    @Override
    public ResponseEntity<List<TrainerMonthlyReportDto>> getMonthlyReports(String email) {
        return ResponseEntity.ok(trainerService.getMonthlyReports(email));
    }

    @Override
    public ResponseEntity<TrainerMonthlyReportDto> updateReportNote(String email, Long reportId, UpdateTrainerReportNoteDto noteDto) {
        return ResponseEntity.ok(trainerService.updateReportNote(email, reportId, noteDto.note()));
    }

    @Override
    public ResponseEntity<TrainerMonthlyReportDto> approveReport(String email, Long reportId) {
        return ResponseEntity.ok(trainerService.approveReport(email, reportId));
    }

    @Override
    public ResponseEntity<TrainerMonthlyReportDto> addReportItem(Long reportId, CreateReportItemDto itemDto) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(trainerService.addReportItem(reportId, itemDto));
    }

    @Override
    public ResponseEntity<TrainerMonthlyReportDto> updateReportItemMoney(Long itemId, UpdateReportItemMoneyDto moneyDto) {
        return ResponseEntity.ok(trainerService.updateReportItemMoney(itemId, moneyDto));
    }

    @Override
    public ResponseEntity<TrainerMonthlyReportDto> deleteReportItem(Long itemId) {
        return ResponseEntity.ok(trainerService.deleteReportItem(itemId));
    }

    @Override
    public ResponseEntity<TrainerMonthlyReportDto> markReportPaid(Long reportId) {
        return ResponseEntity.ok(trainerService.markReportPaid(reportId));
    }
}
