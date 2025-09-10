package co.com.pragma.api;

import co.com.pragma.api.dto.LoanRequestDto;
import co.com.pragma.api.dto.RequestUpdateStatusDto;
import co.com.pragma.api.mapper.RequestDtoMapper;
import co.com.pragma.api.mapper.RequestForReviewMapper;
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
    private final RequestForReviewMapper requestForReviewMapper;

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

    public Mono<ServerResponse> listRequestsForReview(ServerRequest request) {
        int page = request.queryParam(Constants.PAGE)
                .map(Integer::parseInt)
                .orElse(Constants.ZERO_DEFAULT);

        int size = request.queryParam(Constants.SIZE)
                .map(Integer::parseInt)
                .orElse(Constants.TEN_DEFAULT);

        return useCase.listRequestsForReview(page, size)
                .map(requestForReviewMapper::toDto)
                .collectList()
                .flatMap(dtoList -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(dtoList))
                .doOnNext(req -> log.info(Constants.LOG_SUCCESSFUL_REQUEST))
                .doOnError(err -> log.error(Constants.LOG_ERROR_HANDLER));
    }

    public Mono<ServerResponse> updateRequestStatus(ServerRequest request) {
        log.info(Constants.LOG_UPDATE_REQUEST_STATUS);
        return request.bodyToMono(RequestUpdateStatusDto.class)
                .flatMap(dto -> useCase.updateRequestStatus(dto.getRequestId(), dto.getDecision()))
                .flatMap(updatedRequest -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestDtoMapper.toResponse(updatedRequest)))
                .doOnNext(res -> log.info(Constants.LOG_SUCCESSFUL_UPDATE))
                .doOnError(err -> log.error(Constants.LOG_ERROR_HANDLER, err.getMessage()));
    }

}

