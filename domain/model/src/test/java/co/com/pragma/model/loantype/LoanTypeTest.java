package co.com.pragma.model.loantype;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoanTypeTest {

    @Test
    void testLoanTypeAllArgsConstructorAndGetters() {
        LoanType loanType = new LoanType(1L, "Personal", 1000, 50000, 10, true);

        assertEquals(1L, loanType.getId());
        assertEquals("Personal", loanType.getName());
        assertEquals(1000, loanType.getMinAmount());
        assertEquals(50000, loanType.getMaxAmount());
        assertEquals(10, loanType.getInterestRate());
        assertTrue(loanType.getAutomaticValidation());
    }

    @Test
    void testLoanTypeNoArgsConstructorAndSetters() {
        LoanType loanType = new LoanType();
        loanType.setId(2L);
        loanType.setName("Mortgage");
        loanType.setMinAmount(100000);
        loanType.setMaxAmount(1000000);
        loanType.setInterestRate(5);
        loanType.setAutomaticValidation(false);

        assertEquals(2L, loanType.getId());
        assertEquals("Mortgage", loanType.getName());
        assertEquals(100000, loanType.getMinAmount());
        assertEquals(1000000, loanType.getMaxAmount());
        assertEquals(5, loanType.getInterestRate());
        assertFalse(loanType.getAutomaticValidation());
    }

    @Test
    void testLoanTypeBuilder() {
        LoanType loanType = LoanType.builder()
                .id(3L)
                .name("Vehicle")
                .minAmount(5000)
                .maxAmount(250000)
                .interestRate(8)
                .automaticValidation(true)
                .build();

        assertEquals(3L, loanType.getId());
        assertEquals("Vehicle", loanType.getName());
        assertEquals(5000, loanType.getMinAmount());
        assertEquals(250000, loanType.getMaxAmount());
        assertEquals(8, loanType.getInterestRate());
        assertTrue(loanType.getAutomaticValidation());
    }
}