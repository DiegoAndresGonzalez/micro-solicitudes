package co.com.pragma.api.dto;

public record RequestForReviewResponseDto(
        Integer amount,
        String loanTerm,
        String email,
        String fullName,
        String loanTypeName,
        Integer interestRate,
        String statusName,
        Double baseSalary,
        Double totalApprovedDebt
) {}
