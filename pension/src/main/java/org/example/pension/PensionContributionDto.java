package org.example.pension;

public record PensionContributionDto(String email, Long employeeContribution, Long companyContribution, Long balance) {
}
