package co.com.pragma.usecase.petition;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.request.Request;
import co.com.pragma.model.request.gateways.RequestRepository;
import co.com.pragma.model.user.UserGateway;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.usecase.petition.exceptions.InvalidDataException;
import co.com.pragma.usecase.petition.exceptions.LoanTypeNotFoundException;
import co.com.pragma.usecase.petition.exceptions.StatusNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UseCaseTest {

    private UserGateway userGateway;
    private LoanTypeRepository loanTypeRepository;
    private StatusRepository statusRepository;
    private RequestRepository requestRepository;
    private UseCase useCase;

    @BeforeEach
    void setUp() {
        userGateway = Mockito.mock(UserGateway.class);
        loanTypeRepository = Mockito.mock(LoanTypeRepository.class);
        statusRepository = Mockito.mock(StatusRepository.class);
        requestRepository = Mockito.mock(RequestRepository.class);

        useCase = new UseCase(userGateway, loanTypeRepository, statusRepository, requestRepository);
    }

    private Request buildValidRequest() {
        Request request = new Request();
        request.setAmount(5000);
        request.setLoanTerm("12 meses");
        request.setLoanTypeId(1L);
        return request;
    }

    private LoanType buildValidLoanType() {
        return LoanType.builder()
                .id(1L)
                .name("Personal")
                .minAmount(1000)
                .maxAmount(10000)
                .interestRate(10)
                .automaticValidation(true)
                .build();
    }

    private Status buildValidStatus() {
        return Status.builder()
                .id(1L)
                .name("CREATED")
                .description("Initial status")
                .build();
    }

    @Test
    void createRequest_successful() {
        Request request = buildValidRequest();
        LoanType loanType = buildValidLoanType();
        Status status = buildValidStatus();

        when(userGateway.findUserByDocumentId("123"))
                .thenReturn(Mono.just("test@example.com"));
        when(loanTypeRepository.findById(1L))
                .thenReturn(Mono.just(loanType));
        when(statusRepository.findByName(any()))
                .thenReturn(Mono.just(status));
        when(requestRepository.createRequest(any(Request.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.createRequest(request, "123"))
                .assertNext(result -> {
                    assert result.getEmail().equals("test@example.com");
                    assert result.getStatusId().equals(1L);
                })
                .verifyComplete();

        verify(requestRepository).createRequest(any(Request.class));
    }

    @Test
    void createRequest_invalidData_shouldFail() {
        Request invalid = new Request();

        assertThrows(InvalidDataException.class,
                () -> useCase.createRequest(invalid, "").block());

        verifyNoInteractions(userGateway, loanTypeRepository, statusRepository, requestRepository);
    }


    @Test
    void createRequest_loanTypeNotFound_shouldFail() {
        Request request = buildValidRequest();

        when(userGateway.findUserByDocumentId("123"))
                .thenReturn(Mono.just("test@example.com"));
        when(loanTypeRepository.findById(1L))
                .thenReturn(Mono.empty());
        when(statusRepository.findByName(any()))
                .thenReturn(Mono.just(buildValidStatus()));
        StepVerifier.create(useCase.createRequest(request, "123"))
                .expectError(LoanTypeNotFoundException.class)
                .verify();

        verify(requestRepository, never()).createRequest(any());
    }

    @Test
    void createRequest_amountBelowMin_shouldFail() {
        Request request = buildValidRequest();
        request.setAmount(500);

        when(userGateway.findUserByDocumentId("123"))
                .thenReturn(Mono.just("test@example.com"));
        when(loanTypeRepository.findById(1L))
                .thenReturn(Mono.just(buildValidLoanType()));
        when(statusRepository.findByName(any()))
                .thenReturn(Mono.just(buildValidStatus()));

        StepVerifier.create(useCase.createRequest(request, "123"))
                .expectError(InvalidDataException.class)
                .verify();

        verify(requestRepository, never()).createRequest(any());
    }

    @Test
    void createRequest_amountAboveMax_shouldFail() {
        Request request = buildValidRequest();
        request.setAmount(20000);

        when(userGateway.findUserByDocumentId("123"))
                .thenReturn(Mono.just("test@example.com"));
        when(loanTypeRepository.findById(1L))
                .thenReturn(Mono.just(buildValidLoanType()));
        when(statusRepository.findByName(any()))
                .thenReturn(Mono.just(buildValidStatus()));

        StepVerifier.create(useCase.createRequest(request, "123"))
                .expectError(InvalidDataException.class)
                .verify();

        verify(requestRepository, never()).createRequest(any());
    }

    @Test
    void createRequest_statusNotFound_shouldFail() {
        Request request = buildValidRequest();

        when(userGateway.findUserByDocumentId("123"))
                .thenReturn(Mono.just("test@example.com"));
        when(loanTypeRepository.findById(1L))
                .thenReturn(Mono.just(buildValidLoanType()));
        when(statusRepository.findByName(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.createRequest(request, "123"))
                .expectError(StatusNotFoundException.class)
                .verify();

        verify(requestRepository, never()).createRequest(any());
    }
}
