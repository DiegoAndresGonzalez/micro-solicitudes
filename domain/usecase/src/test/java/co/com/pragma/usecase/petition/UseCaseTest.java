package co.com.pragma.usecase.petition;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.notification.gateways.NotificationPublisher;
import co.com.pragma.model.request.Request;
import co.com.pragma.model.request.gateways.RequestRepository;
import co.com.pragma.model.requestforreview.RequestForReview;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserGateway;
import co.com.pragma.usecase.petition.exceptions.InvalidDataException;
import co.com.pragma.usecase.petition.exceptions.LoanTypeNotFoundException;
import co.com.pragma.usecase.petition.exceptions.RequestException;
import co.com.pragma.usecase.petition.exceptions.StatusNotFoundException;
import co.com.pragma.usecase.petition.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private UseCase useCase;

    private Request request;
    private LoanType loanType;
    private Status status;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = Request.builder()
                .id(1L)
                .amount(2000)
                .loanTerm("12 months")
                .loanTypeId(1L)
                .statusId(1L)
                .email("diego@email.com")
                .build();

        loanType = LoanType.builder()
                .id(1L)
                .name("Personal")
                .minAmount(1000)
                .maxAmount(5000)
                .interestRate(12)
                .automaticValidation(true)
                .build();

        status = Status.builder()
                .id(1L)
                .name("PENDING")
                .description("Pending review")
                .build();

        user = User.builder()
                .documentId("123")
                .fullName("Diego González")
                .email("diego@email.com")
                .baseSalary(3000.0)
                .build();
    }

    @Test
    void createRequest_successful() {
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
        when(userGateway.findUserByDocumentId("123")).thenReturn(Mono.just("otro@email.com"));

        StepVerifier.create(useCase.createRequest(request, "123", "diego@email.com"))
                .expectError(InvalidDataException.class)
                .verify();
    }

    @Test
    void createRequest_loanTypeNotFound_shouldFail() {
        request.setLoanTypeId(99L);

        when(userGateway.findUserByDocumentId(anyString())).thenReturn(Mono.just("diego@email.com"));
        when(loanTypeRepository.findById(99L)).thenReturn(Mono.empty());
        when(statusRepository.findByName(anyString())).thenReturn(Mono.just(status));

        StepVerifier.create(useCase.createRequest(request, "123", "diego@email.com"))
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }

    @Test
    void createRequest_statusNotFound_shouldFail() {
        when(userGateway.findUserByDocumentId("123")).thenReturn(Mono.just("diego@email.com"));
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(loanType));
        when(statusRepository.findByName(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.createRequest(request, "123", "diego@email.com"))
                .expectError(StatusNotFoundException.class)
                .verify();
    }

    @Test
    void listRequestsForReview_successful() {
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

    @Test
    void updateRequestStatus_successful() {
        when(requestRepository.findById(1L)).thenReturn(Mono.just(request));
        when(statusRepository.findByName(Constants.STATUS_APPROVED)).thenReturn(Mono.just(status));
        when(requestRepository.updateRequest(any(Request.class))).thenReturn(Mono.just(request));
        when(userGateway.findByEmail(request.getEmail())).thenReturn(Mono.just(user));
        when(loanTypeRepository.findById(request.getLoanTypeId())).thenReturn(Mono.just(loanType));
        when(notificationPublisher.publishNotification(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateRequestStatus(1L, Constants.STATUS_APPROVED))
                .expectNextMatches(r -> r.getId().equals(1L) && r.getStatusId().equals(status.getId()))
                .verifyComplete();

        verify(notificationPublisher, times(1)).publishNotification(any());
    }

    @Test
    void updateRequestStatus_requestNotFound_shouldFail() {
        when(requestRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateRequestStatus(99L, Constants.STATUS_APPROVED))
                .expectErrorMatches(e -> e instanceof RequestException &&
                        e.getMessage().equals(Constants.REQUEST_NOT_FOUND))
                .verify();
    }

    @Test
    void updateRequestStatus_statusNotFound_shouldFail() {
        when(requestRepository.findById(1L)).thenReturn(Mono.just(request));
        when(statusRepository.findByName(Constants.STATUS_APPROVED)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateRequestStatus(1L, Constants.STATUS_APPROVED))
                .expectErrorMatches(e -> e instanceof RequestException &&
                        e.getMessage().equals(Constants.STATUS_NOT_FOUND))
                .verify();
    }

    @Test
    void validateLoanType_amountBelowMin_shouldFail() {
        request.setAmount(500);

        when(userGateway.findUserByDocumentId(anyString())).thenReturn(Mono.just("diego@email.com"));
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(loanType));
        when(statusRepository.findByName(anyString())).thenReturn(Mono.just(status));

        StepVerifier.create(useCase.createRequest(request, "123", "diego@email.com"))
                .expectError(InvalidDataException.class)
                .verify();
    }


    @Test
    void validateLoanType_amountAboveMax_shouldFail() {
        request.setAmount(6000);
        when(userGateway.findUserByDocumentId(anyString())).thenReturn(Mono.just("diego@email.com"));
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(loanType));
        when(statusRepository.findByName(Constants.SET_STATUS_FIRST)).thenReturn(Mono.just(status));

        StepVerifier.create(useCase.createRequest(request, "123", "diego@email.com"))
                .expectError(InvalidDataException.class)
                .verify();
    }


    @Test
    void extractFirstName_nullOrBlank_shouldReturnEmpty() throws Exception {
        Method method = UseCase.class.getDeclaredMethod("extractFirstName", String.class);
        method.setAccessible(true);

        String resultNull = (String) method.invoke(useCase, (Object) null);
        String resultBlank = (String) method.invoke(useCase, "   ");

        assertEquals(Constants.EMPTY, resultNull);
        assertEquals(Constants.EMPTY, resultBlank);
    }

    @Test
    void validateRequestData_nullRequest_shouldFail() {
        when(userGateway.findUserByDocumentId(anyString())).thenReturn(Mono.just("diego@email.com"));
        when(loanTypeRepository.findById(anyLong())).thenReturn(Mono.just(loanType));
        when(statusRepository.findByName(anyString())).thenReturn(Mono.just(status));

        StepVerifier.create(useCase.createRequest(null, "123", "mail"))
                .expectError(InvalidDataException.class)
                .verify();
    }

    @Test
    void validateRequestData_blankDocumentId_shouldFail() {
        Request badDocId = Request.builder()
                .amount(2000)
                .loanTerm("12 months")
                .loanTypeId(1L)
                .statusId(1L)
                .email("test@email.com")
                .build();
        when(userGateway.findUserByDocumentId("")).thenReturn(Mono.empty());
        StepVerifier.create(useCase.createRequest(badDocId, "", "mail"))
                .expectError(InvalidDataException.class)
                .verify();
    }

    @Test
    void validateRequestData_zeroAmount_shouldFail() {
        when(userGateway.findUserByDocumentId("123")).thenReturn(Mono.just("diego@email.com"));
        when(loanTypeRepository.findById(1L)).thenReturn(Mono.just(loanType));
        when(statusRepository.findByName(anyString())).thenReturn(Mono.just(status));

        request.setAmount(0);
        StepVerifier.create(useCase.createRequest(request, "123", "diego@email.com"))
                .expectError(InvalidDataException.class)
                .verify();
    }

    @Test
    void validateDecision_invalid_shouldFail() {
        StepVerifier.create(useCase.updateRequestStatus(1L, "UNKNOWN"))
                .expectErrorMatches(e -> e instanceof RequestException &&
                        e.getMessage().equals(Constants.INVALID_STATUS_UPDATE))
                .verify();
    }

    @Test
    void listRequestsForReview_repositoryError_shouldFail() {
        when(statusRepository.findByName(anyString())).thenReturn(Mono.just(status));
        when(loanTypeRepository.findByAutomaticValidation(false)).thenReturn(Flux.just(loanType));
        when(requestRepository.findByStatusInOrLoanTypeInAndPaginated(anyList(), anyList(), anyInt(), anyInt()))
                .thenReturn(Flux.error(new RuntimeException("DB error")));

        StepVerifier.create(useCase.listRequestsForReview(0, 10))
                .expectErrorMatches(e -> e instanceof RequestException &&
                        e.getMessage().equals(Constants.FAILED_REQUEST))
                .verify();
    }
}