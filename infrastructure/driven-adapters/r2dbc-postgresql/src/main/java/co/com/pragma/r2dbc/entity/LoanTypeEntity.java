package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "loan_type")
public class LoanTypeEntity {

    @Id
    private Long id;
    private String name;
    private Integer minAmount;
    private Integer maxAmount;
    private Integer interestRate;
    private Boolean automaticValidation;

}
