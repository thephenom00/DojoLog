package cz.fel.cvut.attendance.service.api;

import cz.fel.cvut.attendance.service.model.PagedTrainingUnitDto;
import cz.fel.cvut.attendance.service.model.TrainerDto;
import cz.fel.cvut.attendance.service.model.trainer.CreateReportItemDto;
import cz.fel.cvut.attendance.service.model.trainer.TrainerMonthlyReportDto;
import cz.fel.cvut.attendance.service.model.trainer.TrainerReportDto;
import cz.fel.cvut.attendance.service.model.trainer.UpdateReportItemMoneyDto;
import cz.fel.cvut.attendance.service.model.trainer.UpdateTrainerReportNoteDto;
import cz.fel.cvut.attendance.service.model.TrainingUnitDto;
import org.springframework.http.HttpStatus;
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

@RequestMapping("/trainer")
public interface TrainerApi {
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    ResponseEntity<List<TrainerDto>> getTrainers();

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/update")
    ResponseEntity<TrainerDto> updateTrainer(@PathVariable Long id, @RequestBody TrainerDto trainerDto);

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTrainer(@PathVariable Long id);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value="/{email}/training-unit/upcoming")
    ResponseEntity<List<TrainingUnitDto>> getUpcomingTrainingUnits(@PathVariable String email);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value="/{email}/training-unit/past")
    ResponseEntity<PagedTrainingUnitDto> getPastTrainingUnits(
            @PathVariable String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    );

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value="/{email}/report/current-month")
    ResponseEntity<List<TrainerReportDto>> getCurrentMonthReport(@PathVariable String email);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value="/{email}/report/monthly")
    ResponseEntity<List<TrainerMonthlyReportDto>> getMonthlyReports(@PathVariable String email);

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value="/{email}/report/{reportId}/note")
    ResponseEntity<TrainerMonthlyReportDto> updateReportNote(
            @PathVariable String email,
            @PathVariable Long reportId,
            @RequestBody UpdateTrainerReportNoteDto noteDto
    );

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value="/{email}/report/{reportId}/approve")
    ResponseEntity<TrainerMonthlyReportDto> approveReport(@PathVariable String email, @PathVariable Long reportId);

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value="/report/{reportId}/item")
    ResponseEntity<TrainerMonthlyReportDto> addReportItem(
            @PathVariable Long reportId,
            @RequestBody CreateReportItemDto itemDto
    );

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value="/report/item/{itemId}/money")
    ResponseEntity<TrainerMonthlyReportDto> updateReportItemMoney(
            @PathVariable Long itemId,
            @RequestBody UpdateReportItemMoneyDto moneyDto
    );

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value="/report/item/{itemId}")
    ResponseEntity<TrainerMonthlyReportDto> deleteReportItem(@PathVariable Long itemId);

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value="/report/{reportId}/paid")
    ResponseEntity<TrainerMonthlyReportDto> markReportPaid(@PathVariable Long reportId);
}
