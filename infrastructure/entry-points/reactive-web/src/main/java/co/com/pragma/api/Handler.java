package co.com.pragma.api;

import co.com.pragma.api.dto.LoanRequestDto;
import co.com.pragma.api.mapper.RequestDtoMapper;
import co.com.pragma.api.utils.Constants;
import co.com.pragma.usecase.petition.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {

    private final UseCase useCase;
    private final RequestDtoMapper requestDtoMapper;

    public Mono<ServerResponse> createLoanRequest(ServerRequest request) {
        log.info(Constants.LOG_LOAN_REQUEST);
        return request.principal()
                .cast(Authentication.class)
                .flatMap(auth ->
                        request.bodyToMono(LoanRequestDto.class)
                                .flatMap(dto -> {
                                    String emailFromToken = auth.getName();
                                    return useCase.createRequest(
                                            requestDtoMapper.toModel(dto),
                                            dto.documentId(),
                                            emailFromToken
                                    );
                                })
                )
                .doOnNext(req -> log.info(Constants.LOG_SUCCESSFUL_REQUEST))
                .doOnError(err -> log.error(Constants.LOG_ERROR_HANDLER, err.getMessage()))
                .flatMap(req -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestDtoMapper.toResponse(req)));
    }

}

