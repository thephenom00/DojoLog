package cz.fel.cvut.attendance.service.model;

import java.util.List;

public record PagedNewsDto(
        List<NewsDto> content,
        int currentPage,
        int totalPages,
        long totalElements
) {}
