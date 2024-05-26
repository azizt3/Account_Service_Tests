package org.example.pension;

import org.example.pension.helper.PensionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.example.pension.helper.PensionBuilder.buildPensionContributionDto;

@Service
public class PensionService {

    PensionRepository pensionRepository;
    PensionBuilder pensionBuilder;

    @Autowired
    public PensionService(PensionRepository pensionRepository, PensionBuilder pensionBuilder) {
        this.pensionRepository = pensionRepository;
        this.pensionBuilder = pensionBuilder;

    }
    public PensionContributionDto makeContribution(PaymentRequest salaryPayment) {
        Pension pension = pensionRepository.findByEmail(salaryPayment.employee())
                .orElseThrow(() -> new RuntimeException());

        PensionContributionDto pensionContribution = buildPensionContributionDto(
                pension,
                salaryPayment.salary()
        );

        pension.setBalance(pensionContribution.balance());
        pensionRepository.save(pension);
        return pensionContribution;
    }

    public PensionDto registerPension(UserDto newUser) {
        return null;
    }

    public List<Pension> retrievePensions() {
        if (pensionRepository.count() ==0) return List.of(new Pension());
        return pensionRepository.findAll();
    }
}
