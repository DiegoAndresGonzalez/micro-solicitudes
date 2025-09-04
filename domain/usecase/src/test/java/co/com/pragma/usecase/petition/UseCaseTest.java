package co.com.pragma.usecase.petition;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.request.Request;
import co.com.pragma.model.request.gateways.RequestRepository;
import co.com.pragma.model.requestforreview.RequestForReview;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserGateway;
import co.com.pragma.usecase.petition.exceptions.InvalidDataException;
import co.com.pragma.usecase.petition.exceptions.LoanTypeNotFoundException;
import co.com.pragma.usecase.petition.exceptions.StatusNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class UseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private UseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Request buildRequest() {
        Request request = new Request();
        request.setId(1L);
        request.setAmount(2000);
        request.setLoanTerm("12 months");
        request.setLoanTypeId(1L);
        return request;
    }

    private LoanType buildLoanType() {
        LoanType loanType = new LoanType();
        loanType.setId(1L);
        loanType.setName("Personal");
        loanType.setMinAmount(1000);
        loanType.setMaxAmount(5000);
        loanType.setInterestRate(12);
        loanType.setAutomaticValidation(true);
        return loanType;
    }

    private Status buildStatus() {
        Status status = new Status();
        status.setId(1L);
        status.setName("PENDING");
        status.setDescription("Pending review");
        return status;
    }

    private User buildUser() {
        User user = new User();
        user.setDocumentId("123");
        user.setFullName("Diego González");
        user.setEmail("diego@email.com");
        user.setBaseSalary(3000.0);
        return user;
    }

    @Test
    void createRequest_successful() {
        Request request = buildRequest();
        LoanType loanType = buildLoanType();
        Status status = buildStatus();

        when(userGateway.findUserByDocumentId("123")).thenReturn(Mono.just("diego@email.com"));
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(loanType));
        when(statusRepository.findByName(anyString())).thenReturn(Mono.just(status));
        when(requestRepository.createRequest(any(Request.class))).thenReturn(Mono.just(request));

        StepVerifier.create(useCase.createRequest(request, "123", "diego@email.com"))
                .expectNextMatches(r -> r.getAmount().equals(2000) && r.getLoanTypeId().equals(1L))
                .verifyComplete();
    }

    @Test
    void createRequest_invalidEmail_shouldFail() {
        Request request = buildRequest();

        when(userGateway.findUserByDocumentId("123")).thenReturn(Mono.just("otro@email.com"));

        StepVerifier.create(useCase.createRequest(request, "123", "diego@email.com"))
                .expectError(InvalidDataException.class)
                .verify();
    }

    @Test
    void createRequest_loanTypeNotFound_shouldFail() {
        Request request = Request.builder()
                .id(1L)
                .amount(5000)
                .email("test@example.com")
                .loanTerm("12")
                .statusId(1L)
                .loanTypeId(99L)
                .build();

        when(userGateway.findUserByDocumentId(anyString())).thenReturn(Mono.just("test@example.com"));
        when(userGateway.findByEmail(anyString())).thenReturn(Mono.just(User.builder()
                .documentId("123")
                .fullName("Test User")
                .email("test@example.com")
                .baseSalary(2000.0)
                .build()));

        when(loanTypeRepository.findById(99L)).thenReturn(Mono.empty());

        when(statusRepository.findByName(anyString())).thenReturn(Mono.just(
                Status.builder().id(1L).name("PENDING").description("Pending").build()
        ));

        StepVerifier.create(useCase.createRequest(request, "123", "test@example.com"))
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }


    @Test
    void createRequest_statusNotFound_shouldFail() {
        Request request = buildRequest();
        LoanType loanType = buildLoanType();

        when(userGateway.findUserByDocumentId("123")).thenReturn(Mono.just("diego@email.com"));
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(loanType));
        when(statusRepository.findByName(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.createRequest(request, "123", "diego@email.com"))
                .expectError(StatusNotFoundException.class)
                .verify();
    }

    @Test
    void listRequestsForReview_successful() {
        // Arrange
        Status status = Status.builder()
                .id(1L)
                .name("PENDING")
                .description("Pendiente de aprobación")
                .build();

        LoanType loanType = LoanType.builder()
                .id(10L)
                .name("Personal")
                .minAmount(500)
                .maxAmount(5000)
                .interestRate(10)
                .automaticValidation(false)
                .build();

        User user = User.builder()
                .documentId("123456")
                .fullName("Diego González")
                .email("diego@email.com")
                .baseSalary(2500.0)
                .build();

        Request request = Request.builder()
                .id(100L)
                .amount(2000)
                .email(user.getEmail())
                .loanTerm("12M")
                .statusId(status.getId())
                .loanTypeId(loanType.getId())
                .build();

        when(statusRepository.findByName(anyString())).thenReturn(Mono.just(status));

        when(loanTypeRepository.findByAutomaticValidation(false)).thenReturn(Flux.just(loanType));

        when(requestRepository.findByStatusInOrLoanTypeInAndPaginated(anyList(), anyList(), anyInt(), anyInt()))
                .thenReturn(Flux.just(request));

        when(userGateway.findByEmail(eq(request.getEmail()))).thenReturn(Mono.just(user));
        when(loanTypeRepository.findById(request.getLoanTypeId())).thenReturn(Mono.just(loanType));
        when(statusRepository.findById(request.getStatusId())).thenReturn(Mono.just(status));
        when(requestRepository.sumApprovedDebtsByEmail(request.getEmail())).thenReturn(Mono.just(1000.0));

        StepVerifier.create(useCase.listRequestsForReview(0, 10))
                .expectNextMatches(rfr ->
                        rfr instanceof RequestForReview &&
                                rfr.getRequest().getAmount().equals(2000) &&
                                rfr.getUser().getEmail().equals("diego@email.com") &&
                                rfr.getLoanType().getName().equals("Personal") &&
                                rfr.getStatus().getName().equals("PENDING") &&
                                rfr.getTotalApprovedDebt().equals(1000.0)
                )
                .verifyComplete();
    }


    @Test
    void listRequestsForReview_invalidPagination_shouldFail() {
        StepVerifier.create(useCase.listRequestsForReview(-1, 0))
                .expectError(InvalidDataException.class)
                .verify();
    }
}
