package org.example.pension;

import org.example.pension.helper.PensionBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PensionServiceUnitTest {

    private PensionService pensionService;
    @Mock
    PensionRepository pensionRepository;
    @Mock
    PensionBuilder pensionBuilder;

    @BeforeEach
    void setUp(){
        pensionService = new PensionService(pensionRepository, pensionBuilder);
    }

    @Test
    void givenPaymentDetails_whenMakingContribution_thenVerifyPensionRepositoryInvokedOnce(){
        PaymentRequest payment = new PaymentRequest("tabbish.aziz@acme.com", "01-2024", 300000L);
        Pension mockPension = new Pension(  "tabbish.aziz@acme.com", "DC", 150000L, 8L);
        when(pensionRepository.findByEmail("tabbish.aziz@acme.com")).thenReturn(Optional.of(mockPension));
        PensionContributionDto pension = pensionService.makeContribution(payment);
        verify(pensionRepository, times(1)).findByEmail("tabbish.aziz@acme.com");
    }

    @Test
    void givenPaymentDetails_whenMakingContribution_thenReturnPensionDetails(){
        PaymentRequest payment = new PaymentRequest("tabbish.aziz@acme.com", "01-2024", 300000L);
        Pension mockPension = new Pension(  "tabbish.aziz@acme.com", "DC", 150000L, 8L);
        Pension updatedPension = new Pension ("tabbish.aziz@acme.com", "DC", 150240L, 8L);

        PensionContributionDto deposit = new PensionContributionDto(
                "tabbish.aziz@acme.com",
                60L,
                180L,
                150240L
        );

        when(pensionRepository.findByEmail("tabbish.aziz@acme.com")).thenReturn(Optional.of((mockPension)));
        when(pensionRepository.save(any(Pension.class))).thenReturn(updatedPension);
        PensionContributionDto pension = pensionService.makeContribution(payment);
        verify(pensionRepository, times(1)).findByEmail("tabbish.aziz@acme.com");
        assertEquals(deposit, pension);
    }
}
