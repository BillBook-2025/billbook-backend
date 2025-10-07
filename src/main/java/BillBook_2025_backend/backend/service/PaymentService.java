package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.dto.IamportClient;
import BillBook_2025_backend.backend.dto.PaymentInfo;
import BillBook_2025_backend.backend.entity.*;
import BillBook_2025_backend.backend.repository.PointTransactionRepository;
import BillBook_2025_backend.backend.repository.UserPointsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PointTransactionRepository txRepository;
    private final UserPointsRepository pointsRepository;
    private final IamportClient iamportClient;

    public void confirmPayment(Long userId, String impUid) {
        // 1. 아임포트 결제 정보 조회
        PaymentInfo paymentInfo = iamportClient.getPaymentInfo(impUid);

        // 2. 거래 내역 조회 (pending 상태)
        PointTransaction tx = txRepository.findByImpUid(impUid)
                .orElseThrow(() -> new RuntimeException("거래 내역 없음"));

        // 3. 금액 및 상태 검증
        if (!paymentInfo.getAmount().equals(tx.getAmount())) {
            throw new IllegalStateException("결제 금액 불일치");
        }
        if (!"paid".equals(paymentInfo.getStatus())) {
            throw new IllegalStateException("결제 실패 상태");
        }

        // 4. 포인트 적립
        UserPoints points = pointsRepository.findById(userId)
                .orElse(new UserPoints(userId, 0));
        points.charge(tx.getAmount());
        pointsRepository.save(points);

        // 5. 거래 내역 상태 업데이트
        tx.setStatus(TransactionStatus.SUCCESS);
        txRepository.save(tx);
    }

    public PaymentInfo readyPayment(PaymentInfo paymentInfo, Long userId) {
        PointTransaction transaction = PointTransaction.builder()
                .userId(userId)
                .amount(paymentInfo.getAmount())
                .impUid(UUID.randomUUID() + "")
                .status(TransactionStatus.PENDING)
                .build();


        PointTransaction save = txRepository.save(transaction);


        PaymentInfo transactionInfo = PaymentInfo.builder()
                .amount(save.getAmount())
                .impUid(save.getImpUid())
                .build();

        return transactionInfo;
    }
}

