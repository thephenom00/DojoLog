package cz.fel.cvut.attendance.service.model.parent;

public record ChildEventStatusDto(
        Long id,
        String firstName,
        String lastName,
        boolean isRegistered
)
{}
