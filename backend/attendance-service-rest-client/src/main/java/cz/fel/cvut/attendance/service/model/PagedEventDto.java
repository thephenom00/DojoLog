package cz.fel.cvut.attendance.service.model;

import java.util.List;

public record PagedEventDto(
        List<EventDto> content,
        int currentPage,
        int totalPages,
        long totalElements
) {}
