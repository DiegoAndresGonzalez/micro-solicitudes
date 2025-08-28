package co.com.pragma.r2dbc;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.r2dbc.entity.LoanTypeEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanTypeReactiveRepositoryAdapterTest {

    @InjectMocks
    LoanTypeReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    LoanTypeReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private LoanType buildValidLoanType() {
        return LoanType.builder()
                .id(1L)
                .name("Personal Loan")
                .minAmount(1000)
                .maxAmount(5000)
                .interestRate(10)
                .automaticValidation(true)
                .build();
    }

    private LoanTypeEntity buildValidLoanTypeEntity() {
        return LoanTypeEntity.builder()
                .id(1L)
                .name("Personal Loan")
                .minAmount(1000)
                .maxAmount(5000)
                .interestRate(10)
                .automaticValidation(true)
                .build();
    }

    @Test
    void mustFindById() {
        LoanType loanType = buildValidLoanType();
        LoanTypeEntity loanTypeEntity = buildValidLoanTypeEntity();

        when(repository.findById(1L)).thenReturn(Mono.just(loanTypeEntity));
        when(mapper.map(loanTypeEntity, LoanType.class)).thenReturn(loanType);

        Mono<LoanType> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(value ->
                        value.getId().equals(1L) &&
                                value.getName().equals("Personal Loan"))
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Mono.empty());

        Mono<LoanType> result = repositoryAdapter.findById(99L);

        StepVerifier.create(result)
                .verifyComplete(); // no emitió nada
    }
}
