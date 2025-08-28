package co.com.pragma.api.dto;

public record LoanRequestDto (String documentId, Long loanTypeId, String loanTerm, Integer amount) {

}
