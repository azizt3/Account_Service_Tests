package usecase.updatepayment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import usecase.addpayment.PaymentRequest;

@RestController
public class UpdatePaymentController {

    UpdatePaymentService updatePaymentService;

    @Autowired
    public UpdatePaymentController (UpdatePaymentService updatePaymentService) {
        this.updatePaymentService = updatePaymentService;
    }

    @PutMapping(path = "/api/acct/payments")
    public ResponseEntity<?> updatePayments(@RequestBody PaymentRequest payment) {
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(updatePaymentService.handlePaymentUpdate(payment));
    }
}
