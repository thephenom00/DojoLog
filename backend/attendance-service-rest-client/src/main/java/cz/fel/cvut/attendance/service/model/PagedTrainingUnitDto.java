package cz.fel.cvut.attendance.service.model;

import java.util.List;

public record PagedTrainingUnitDto(
        List<TrainingUnitDto> content,
        int currentPage,
        int totalPages,
        long totalElements
) {}
