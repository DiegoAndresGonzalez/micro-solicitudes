package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "request")
public class RequestEntity {

    @Id
    private Long id;
    private Integer amount;
    private String email;
    private String loanTerm;
    private Long statusId;
    private Long loanTypeId;

}
