package org.example.pension.helper;

import org.example.pension.Pension;
import org.example.pension.PensionContributionDto;
import org.springframework.stereotype.Component;

import static java.lang.Long.parseLong;

@Component
public class PensionBuilder {

    public static PensionContributionDto buildPensionContributionDto(Pension pension, Long payment){

        Long salary = payment/100;
        Long employeeContribution = (long) (salary*0.02);
        Long companyContribution = employeeContribution*3;
        Long pensionBalance = pension.getBalance() +employeeContribution + companyContribution;

        return new PensionContributionDto(
                pension.getEmail(),
                employeeContribution,
                companyContribution,
                pensionBalance
        );
    }
}
