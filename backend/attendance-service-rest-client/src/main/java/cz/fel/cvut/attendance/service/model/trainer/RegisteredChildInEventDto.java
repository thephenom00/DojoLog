package cz.fel.cvut.attendance.service.model.trainer;

public record RegisteredChildInEventDto(
        Long id,
        String name,
        String note,
        boolean paymentReceived,
        String phoneNumber,
        String email,
        boolean present
) {
}
