package co.com.pragma.model.requestforreview;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.request.Request;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.user.User;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RequestForReview {

    private Request request;
    private User user;
    private LoanType loanType;
    private Status status;
    private Double totalApprovedDebt;
}
