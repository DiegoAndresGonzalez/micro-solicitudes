package co.com.pragma.consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String name;
    private String lastName;
    private LocalDate birthday;
    private String address;
    private String email;
    private String documentId;
    private String phone;
    private Long roleId;
    private Integer baseSalary;

}
