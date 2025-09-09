package cz.cvut.fel.attendance.service.model;

import cz.cvut.fel.attendance.service.enums.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    public Admin(String firstName, String lastName, String email, String phoneNumber, String password) {
        super(firstName, lastName, email, phoneNumber, password, Role.ROLE_ADMIN);
    }
}
