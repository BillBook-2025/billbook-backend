package BillBook_2025_backend.backend.controller;


import BillBook_2025_backend.backend.dto.PaymentInfo;
import BillBook_2025_backend.backend.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/ready")
    public ResponseEntity<PaymentInfo> readyPayment(@RequestBody PaymentInfo paymentInfo, @RequestParam Long userId) {
        PaymentInfo paymentInfo1 = paymentService.readyPayment(paymentInfo, userId);
        return ResponseEntity.ok(paymentInfo1);  //amount랑 uuid 반환
    }

    // 포인트 충전 완료 콜백 처리
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayment(@RequestParam Long userId,
                                                 @RequestParam String impUid) {
        paymentService.confirmPayment(userId, impUid);
        return ResponseEntity.ok("포인트 충전 완료");
    }
}

