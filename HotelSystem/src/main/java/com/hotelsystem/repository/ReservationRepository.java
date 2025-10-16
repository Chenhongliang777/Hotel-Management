package com.hotelsystem.repository;

import com.hotelsystem.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationNumber(String reservationNumber);
    List<Reservation> findByGuestId(Long guestId);
    List<Reservation> findByRoomId(Long roomId);
    List<Reservation> findByStatus(Reservation.ReservationStatus status);
    List<Reservation> findByCheckInDateBetween(LocalDate start, LocalDate end);
    List<Reservation> findByCheckOutDateBetween(LocalDate start, LocalDate end);
}
