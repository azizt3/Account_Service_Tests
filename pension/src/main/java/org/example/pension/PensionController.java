package org.example.pension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PensionController {

    PensionService pensionService;

    @Autowired
    public PensionController(PensionService pensionService) {
        this.pensionService = pensionService;
    }

    @PostMapping(path = "/api/auth/pension/register")
    public ResponseEntity<?> registerPension(@RequestBody UserDto newUser) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(pensionService.registerPension(newUser));
    }

    @GetMapping(path="/api/pension/get")
    public ResponseEntity<?> getPensions(){
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(pensionService.retrievePensions());
    }

    @PutMapping(path = "/api/auth/pension/payment")
    public ResponseEntity<?> makeContribution(@RequestBody PaymentRequest salaryPayment) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(pensionService.makeContribution(salaryPayment));
    }


}
