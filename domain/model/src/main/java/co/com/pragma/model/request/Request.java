package co.com.pragma.model.request;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Request {

    private Long id;
    private Integer amount;
    private String email;
    private String loanTerm;
    private Long statusId;
    private Long loanTypeId;

}
