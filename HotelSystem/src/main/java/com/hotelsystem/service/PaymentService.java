package com.hotelsystem.service;

import com.hotelsystem.entity.PaymentTransaction;
import com.hotelsystem.entity.PaymentTransaction.TransactionStatus;
import com.hotelsystem.entity.PaymentTransaction.TransactionType;
import com.hotelsystem.repository.PaymentTransactionRepository;
import com.hotelsystem.repository.ReservationRepository;
import com.hotelsystem.repository.RoomRepository;
import com.hotelsystem.entity.Reservation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentTransactionRepository transactionRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public PaymentTransaction createPendingPayment(Long reservationId, BigDecimal amount, String note) {
        PaymentTransaction t = new PaymentTransaction();
        t.setReservationId(reservationId);
        t.setAmount(amount);
        t.setType(TransactionType.PAYMENT);
        t.setStatus(TransactionStatus.PENDING);
        t.setNote(note);
        return transactionRepository.save(t);
    }

    public PaymentTransaction createPendingRefund(Long reservationId, BigDecimal amount, String note) {
        PaymentTransaction t = new PaymentTransaction();
        t.setReservationId(reservationId);
        t.setAmount(amount);
        t.setType(TransactionType.REFUND);
        t.setStatus(TransactionStatus.PENDING);
        t.setNote(note);
        return transactionRepository.save(t);
    }

    @Transactional
    public PaymentTransaction markSuccess(Long transactionId, String providerTransactionId) {
        var tOpt = transactionRepository.findById(transactionId);
        if (tOpt.isPresent()) {
            var t = tOpt.get();
            t.setStatus(TransactionStatus.SUCCESS);
            t.setProviderTransactionId(providerTransactionId);
            var saved = transactionRepository.save(t);

            // 当支付成功且为 PAYMENT 类型时，更新预订 paidAmount 并尝试将房间标记为 RESERVED
            if (t.getType() == TransactionType.PAYMENT) {
                // 简单重试以应对乐观锁冲突
                int attempts = 0;
                final int MAX_RETRIES = 3;
                while (true) {
                    try {
                        Reservation r = reservationRepository.findById(t.getReservationId())
                                .orElseThrow(() -> new RuntimeException("预订不存在"));

                        java.math.BigDecimal paid = r.getPaidAmount() == null ? java.math.BigDecimal.ZERO : r.getPaidAmount();
                        paid = paid.add(t.getAmount() == null ? java.math.BigDecimal.ZERO : t.getAmount());
                        r.setPaidAmount(paid);
                        // 将预订状态设为 CONFIRMED（若原先为 PENDING）
                        if (r.getStatus() == null || r.getStatus() == Reservation.ReservationStatus.PENDING) {
                            r.setStatus(Reservation.ReservationStatus.CONFIRMED);
                        }

                        // 如果预订已有房间且房间为 AVAILABLE，则标为 RESERVED；否则若未分配房间，尝试按 preferredRoomType 分配
                        com.hotelsystem.entity.Room room = r.getRoom();
                        if (room != null) {
                            if (room.getStatus() == com.hotelsystem.entity.Room.RoomStatus.AVAILABLE) {
                                room.setStatus(com.hotelsystem.entity.Room.RoomStatus.RESERVED);
                                roomRepository.save(room);
                            }
                        } else if (r.getPreferredRoomType() != null && !r.getPreferredRoomType().isBlank()) {
                            // 查找可用房间
                            var candidates = roomRepository.findByRoomType(r.getPreferredRoomType());
                            if (candidates != null) {
                                for (var cand : candidates) {
                                    if (cand.getStatus() != com.hotelsystem.entity.Room.RoomStatus.AVAILABLE) continue;
                                    // 检查冲突
                                    var conflicts = reservationRepository.findConflictingReservations(cand.getId(), r.getCheckInDate(), r.getCheckOutDate(), null);
                                    if (conflicts == null || conflicts.isEmpty()) {
                                        // 分配并保留
                                        r.setRoom(cand);
                                        cand.setStatus(com.hotelsystem.entity.Room.RoomStatus.RESERVED);
                                        roomRepository.save(cand);
                                        break;
                                    }
                                }
                            }
                        }

                        reservationRepository.save(r);
                        break;
                    } catch (OptimisticLockingFailureException ole) {
                        attempts++;
                        if (attempts >= MAX_RETRIES) throw new RuntimeException("更新预订/分配房间时发生并发冲突，请重试");
                        // 否则重试
                    }
                }
            }

            return saved;
        }
        throw new RuntimeException("交易不存在");
    }

    public PaymentTransaction markFailed(Long transactionId, String note) {
        var tOpt = transactionRepository.findById(transactionId);
        if (tOpt.isPresent()) {
            var t = tOpt.get();
            t.setStatus(TransactionStatus.FAILED);
            t.setNote(note);
            return transactionRepository.save(t);
        }
        throw new RuntimeException("交易不存在");
    }

    public List<PaymentTransaction> getTransactionsByReservation(Long reservationId) {
        return transactionRepository.findByReservationId(reservationId);
    }
}
