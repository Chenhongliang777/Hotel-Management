package com.hotelsystem.repository;

import com.hotelsystem.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    Optional<Guest> findByIdCardNumber(String idCardNumber);
    List<Guest> findByFullNameContainingIgnoreCase(String fullName);
    List<Guest> findByPhone(String phone);
    Boolean existsByIdCardNumber(String idCardNumber);

    Optional<Guest> findByEmail(String email);
    
    // 综合搜索：按姓名、身份证号或手机号搜索
    List<Guest> findByFullNameContainingIgnoreCaseOrIdCardNumberContainingOrPhoneContaining(
            String fullName, String idCardNumber, String phone);
}
