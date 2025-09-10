package co.com.pragma.model.user;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private String documentId;
    private String fullName;
    private String email;
    private Double baseSalary;

}
