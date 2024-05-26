package usecase.addpayment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AddPaymentControler {

    AddPaymentService addPaymentService;

    @Autowired
    public AddPaymentControler(AddPaymentService addPaymentService){
        this.addPaymentService = addPaymentService;
    }
    @PostMapping(path = "/api/acct/payments")
    public ResponseEntity<?> addPayments(
        @NotEmpty(message = "Payments cannot be empty") @RequestBody List<@Valid PaymentRequest> payments) {

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                payments.stream()
                    .map(paymentRequest -> addPaymentService.postPayment(paymentRequest))
                    .collect(Collectors.toList())
            );
    }
}
