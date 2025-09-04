package co.com.pragma.model.requestforreview;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.request.Request;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.user.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestForReviewTest {

    @Test
    void builder_shouldCreateRequestForReview() {
        Request request = Request.builder()
                .id(1L)
                .amount(2000)
                .email("solicitante@email.com")
                .loanTerm("12M")
                .statusId(1L)
                .loanTypeId(10L)
                .build();

        User user = User.builder()
                .documentId("123")
                .fullName("Usuario Test")
                .email("solicitante@email.com")
                .baseSalary(2500.0)
                .build();

        LoanType loanType = LoanType.builder()
                .id(10L)
                .name("Personal")
                .minAmount(500)
                .maxAmount(5000)
                .interestRate(12)
                .automaticValidation(false)
                .build();

        Status status = Status.builder()
                .id(1L)
                .name("PENDING")
                .description("Pendiente")
                .build();

        RequestForReview rfr = RequestForReview.builder()
                .request(request)
                .user(user)
                .loanType(loanType)
                .status(status)
                .totalApprovedDebt(1000.0)
                .build();

        assertThat(rfr.getRequest().getAmount()).isEqualTo(2000);
        assertThat(rfr.getUser().getFullName()).isEqualTo("Usuario Test");
        assertThat(rfr.getLoanType().getName()).isEqualTo("Personal");
        assertThat(rfr.getStatus().getName()).isEqualTo("PENDING");
        assertThat(rfr.getTotalApprovedDebt()).isEqualTo(1000.0);
    }

    @Test
    void toBuilder_shouldCloneAndModify() {
        RequestForReview original = RequestForReview.builder()
                .totalApprovedDebt(1000.0)
                .build();

        RequestForReview modified = original.toBuilder()
                .totalApprovedDebt(2000.0)
                .build();

        assertThat(modified.getTotalApprovedDebt()).isEqualTo(2000.0);
    }
}
