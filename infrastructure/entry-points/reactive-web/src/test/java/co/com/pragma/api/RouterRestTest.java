package co.com.pragma.api;

import co.com.pragma.api.dto.LoanRequestDto;
import co.com.pragma.api.dto.LoanResponseDto;
import co.com.pragma.api.mapper.RequestDtoMapper;
import co.com.pragma.api.mapper.RequestForReviewMapper;
import co.com.pragma.model.request.Request;
import co.com.pragma.model.requestforreview.RequestForReview;
import co.com.pragma.usecase.petition.UseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RouterRestTest {

    private UseCase useCase;
    private RequestDtoMapper requestDtoMapper;
    private WebTestClient webTestClient;
    private RequestForReviewMapper requestForReviewMapper;

    private LoanRequestDto buildLoanRequestDto() {
        return new LoanRequestDto("cliente@test.com", 1L, "12 meses", 5000);
    }

    private Request buildRequest() {
        return Request.builder()
                .id(1L)
                .email("cliente@test.com")
                .loanTypeId(1L)
                .loanTerm("12 meses")
                .amount(5000)
                .build();
    }

    private LoanResponseDto buildLoanResponseDto() {
        return new LoanResponseDto(1L, "5000", "12 meses", "cliente@test.com");
    }

    private RequestForReview buildRequestForReview() {
        return RequestForReview.builder()
                .request(buildRequest())
                .loanType(null)
                .status(null)
                .user(null)
                .totalApprovedDebt(0.0)
                .build();
    }

    @BeforeEach
    void setUp() {
        useCase = Mockito.mock(UseCase.class);
        requestDtoMapper = Mockito.mock(RequestDtoMapper.class);

        Handler handler = new Handler(useCase, requestDtoMapper, requestForReviewMapper);
        RouterRest routerRest = new RouterRest();

        webTestClient = WebTestClient.bindToRouterFunction(routerRest.routerFunction(handler)).build();
    }

    @Test
    void createLoanRequest_success() {
        LoanRequestDto requestDto = buildLoanRequestDto();
        Request request = buildRequest();
        LoanResponseDto responseDto = buildLoanResponseDto();

        when(requestDtoMapper.toModel(any(LoanRequestDto.class))).thenReturn(request);
        when(useCase.createRequest(any(Request.class), any(String.class), any(String.class)))
                .thenReturn(Mono.just(request));
        when(requestDtoMapper.toResponse(any(Request.class))).thenReturn(responseDto);

        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.amount").isEqualTo("5000")
                .jsonPath("$.loanTerm").isEqualTo("12 meses")
                .jsonPath("$.email").isEqualTo("cliente@test.com");
    }

    @Test
    void createLoanRequest_error() {
        LoanRequestDto requestDto = buildLoanRequestDto();
        Request request = buildRequest();

        when(requestDtoMapper.toModel(any(LoanRequestDto.class))).thenReturn(request);
        when(useCase.createRequest(any(Request.class), any(String.class), any(String.class)))
                .thenReturn(Mono.error(new RuntimeException("Error al crear")));

        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void listRequestsForReview_success() {
        RequestForReview reqForReview = buildRequestForReview();

        when(useCase.listRequestsForReview(0, 10))
                .thenReturn(Flux.just(reqForReview));

        webTestClient.get()
                .uri("/api/v1/solicitud?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].request.id").isEqualTo(1)
                .jsonPath("$[0].request.email").isEqualTo("cliente@test.com");
    }

    @Test
    void listRequestsForReview_empty() {
        when(useCase.listRequestsForReview(0, 10)).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/solicitud?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[]");
    }
}
