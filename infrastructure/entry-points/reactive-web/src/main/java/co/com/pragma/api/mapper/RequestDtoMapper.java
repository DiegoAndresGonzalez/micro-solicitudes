package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.LoanRequestDto;
import co.com.pragma.api.dto.LoanResponseDto;
import co.com.pragma.model.request.Request;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RequestDtoMapper {

    Request toModel (LoanRequestDto requestDto);
    LoanResponseDto toResponse (Request request);

}
