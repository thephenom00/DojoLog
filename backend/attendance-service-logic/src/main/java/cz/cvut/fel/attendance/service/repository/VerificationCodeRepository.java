package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.enums.OtpPurpose;
import cz.cvut.fel.attendance.service.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findByEmailAndPurpose(String email, OtpPurpose purpose);

    void deleteByEmailAndPurpose(String email, OtpPurpose purpose);
}

