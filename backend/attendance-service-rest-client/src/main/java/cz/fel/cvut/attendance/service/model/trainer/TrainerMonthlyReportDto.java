package cz.fel.cvut.attendance.service.model.trainer;

import java.util.List;

public record TrainerMonthlyReportDto(
        Long id,
        int year,
        int month,
        String status,
        String note,
        String approvedAt,
        boolean editable,
        int totalMoney,
        List<TrainerReportItemDto> items
) {
}
