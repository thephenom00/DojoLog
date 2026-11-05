package cz.fel.cvut.attendance.service.model;


public record SchoolDto(
        Long id,
        String name,
        String address,
        String city,
        String contactPerson,
        String contactNumber,
        String instructions
) {}
