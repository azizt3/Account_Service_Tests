package dto;

public record PensionContributionDto(String email, Long employeeContribution, Long companyContribution, Long balance) {
}