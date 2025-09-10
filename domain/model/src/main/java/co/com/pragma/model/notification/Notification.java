package co.com.pragma.model.notification;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Notification {
    private Long requestId;
    private String status;
    private String email;
    private String fullName;
    private String loanType;
    private Integer amount;
}
