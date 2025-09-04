package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.RequestForReviewResponseDto;
import co.com.pragma.model.requestforreview.RequestForReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequestForReviewMapper {

    @Mapping(source = "request.amount", target = "amount")
    @Mapping(source = "request.loanTerm", target = "loanTerm")
    @Mapping(source = "request.email", target = "email")
    @Mapping(source = "user.fullName", target = "fullName")
    @Mapping(source = "user.baseSalary", target = "baseSalary")
    @Mapping(source = "totalApprovedDebt", target = "totalApprovedDebt")
    @Mapping(source = "loanType.name", target = "loanTypeName")
    @Mapping(source = "loanType.interestRate", target = "interestRate")
    @Mapping(source = "status.name", target = "statusName")
    RequestForReviewResponseDto toDto(RequestForReview requestForReview);

}
