package cz.cvut.fel.attendance.service.model;

import cz.cvut.fel.attendance.service.enums.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("PARENT")
public class Parent extends User {

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Child> children = new ArrayList<>();

    public Parent(String firstName, String lastName, String email, String phoneNumber, String password) {
        super(firstName, lastName, email, phoneNumber, password, Role.ROLE_PARENT);
    }

    public boolean addChild(Child child) {
        if (!children.contains(child)) {
            this.children.add(child);
            child.setParent(this);
            return true;
        }
        return false;
    }
}
