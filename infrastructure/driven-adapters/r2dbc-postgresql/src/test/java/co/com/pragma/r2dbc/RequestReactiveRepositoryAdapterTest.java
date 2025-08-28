package co.com.pragma.r2dbc;

import co.com.pragma.model.request.Request;
import co.com.pragma.r2dbc.entity.RequestEntity;
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
class RequestReactiveRepositoryAdapterTest {

    @InjectMocks
    RequestReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    RequestReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private Request buildValidRequest() {
        return Request.builder()
                .id(1L)
                .amount(2000)
                .email("diego@test.com")
                .loanTerm("12 months")
                .statusId(10L)
                .loanTypeId(5L)
                .build();
    }

    private RequestEntity buildValidRequestEntity() {
        return RequestEntity.builder()
                .id(1L)
                .amount(2000)
                .email("diego@test.com")
                .loanTerm("12 months")
                .statusId(10L)
                .loanTypeId(5L)
                .build();
    }

    @Test
    void mustCreateRequestSuccessfully() {
        Request request = buildValidRequest();
        RequestEntity entity = buildValidRequestEntity();

        when(mapper.map(request, RequestEntity.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Request.class)).thenReturn(request);

        Mono<Request> result = repositoryAdapter.createRequest(request);

        StepVerifier.create(result)
                .expectNextMatches(saved ->
                        saved.getId().equals(1L) &&
                                saved.getEmail().equals("diego@test.com") &&
                                saved.getAmount().equals(2000)
                )
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenRepositoryFailsToSave() {
        Request request = buildValidRequest();
        RequestEntity entity = buildValidRequestEntity();

        when(mapper.map(request, RequestEntity.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.empty());

        Mono<Request> result = repositoryAdapter.createRequest(request);

        StepVerifier.create(result)
                .verifyComplete();
    }
}