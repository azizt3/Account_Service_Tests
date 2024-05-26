package usecase.getpayment;

import entity.UserAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Optional;

@RestController
public class GetPaymentController {

    GetPaymentService getPaymentService;

    @Autowired
    public GetPaymentController(GetPaymentService getPaymentService){
        this.getPaymentService=getPaymentService;
    }

    @GetMapping(path = "/api/empl/payment")
    public ResponseEntity<?> getPayment (
        @RequestParam(required = false) Optional<String> period,
        @AuthenticationPrincipal UserAdapter user) throws ParseException {

        if (period.isPresent()){
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getPaymentService.handleGetPayment(period.get()));
        }
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(getPaymentService.handleGetAllPayments());
    }

}
