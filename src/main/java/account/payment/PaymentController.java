package account.payment;

import account.payment.request.PaymentRequest;
import account.user.UserAdapter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    public PaymentController (PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @GetMapping(path = "/api/empl/payment")
    public ResponseEntity<?> getPayment (
        @RequestParam(required = false)  Optional<String> period,
        @AuthenticationPrincipal UserAdapter user) throws ParseException {

        if (period.isPresent()){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentService.handleGetPayment(period.get()));
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentService.handleGetAllPayments());
    }

    @PutMapping(path = "/api/acct/payments")
    public ResponseEntity<?> updatePayments(@RequestBody PaymentRequest payment) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentService.handlePaymentUpdate(payment));
    }

    @PostMapping(path = "/api/acct/payments")
    public ResponseEntity<?> addPayments(
        @NotEmpty(message = "Payments cannot be empty") @RequestBody List<@Valid PaymentRequest> payments) {

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        payments.stream()
                        .map(paymentRequest -> paymentService.postPayment(paymentRequest))
                        .collect(Collectors.toList())
                );
    }
}




