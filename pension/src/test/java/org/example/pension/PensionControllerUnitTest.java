package org.example.pension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PensionControllerUnitTest {

    private PensionController pensionController;

    @Autowired
    MockMvc mockMvc;

    List<Pension> pensions = new ArrayList<>();

    @Mock
    PensionService pensionService;

    @BeforeEach
    public void setUp(){
        pensionController = new PensionController(pensionService);
    }

    @Test
    void givenPaymentDetails_whenDepositingToPension_thenReturnUpdatedPensionBalance() throws Exception {

        Pension tabbishPension = new Pension (
                "tabbish.aziz@acme.com",
                "DC",
                150000L,
                2880L
        );

        PaymentRequest salaryPayment = new PaymentRequest(
                "tabbish.aziz@acme.com",
                "01-2024",
                300000L
        );

        PensionContributionDto updatedPension = new PensionContributionDto(
                "tabbish.aziz@acme.com",
                60L,
                180L,
                150240L
        );

        when(pensionService.makeContribution(salaryPayment))
                .thenReturn(updatedPension);
        ResponseEntity<?> response = pensionController.makeContribution(salaryPayment);
        assertEquals(updatedPension, response.getBody());
    }

    @Test
    void givenNewUserDetails_whenEnrollingInPension_thenReturnNewPensionDetails() throws Exception{

        UserDto newUser = new UserDto(1L, "tabbish", "aziz", "tabbish.aziz@acme.com");

        PensionDto pensionDetails = new PensionDto(
                "tabbish.aziz@acme.com",
                "DC",
                0L,
                1L
        );

        when(pensionService.registerPension(newUser)).thenReturn(pensionDetails);
        ResponseEntity<?> response = pensionController.registerPension(newUser);
        assertEquals(pensionDetails, response.getBody());
    }



}
