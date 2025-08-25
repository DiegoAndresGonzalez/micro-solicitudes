package co.com.pragma.model.petition;
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
public class Petition {

    private Long id;
    private Integer amount;
    private String email;
    private Long statusId;
    private Long loanTypeId;

}
