package co.com.pragma.r2dbc;

import co.com.pragma.model.status.Status;
import co.com.pragma.r2dbc.entity.StatusEntity;
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
class StatusReactiveRepositoryAdapterTest {

    @InjectMocks
    StatusReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    StatusReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private Status buildValidStatus() {
        return Status.builder()
                .id(1L)
                .name("Pendiente")
                .build();
    }

    private StatusEntity buildValidStatusEntity() {
        return StatusEntity.builder()
                .id(1L)
                .name("Pendiente")
                .build();
    }

    @Test
    void mustFindByNameSuccessfully() {
        StatusEntity entity = buildValidStatusEntity();
        Status status = buildValidStatus();

        when(repository.findByName("Pendiente")).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Status.class)).thenReturn(status);

        Mono<Status> result = repositoryAdapter.findByName("Pendiente");

        StepVerifier.create(result)
                .expectNextMatches(found ->
                        found.getId().equals(1L) &&
                                found.getName().equals("Pendiente")
                )
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenNotFound() {
        when(repository.findByName("Desconocido")).thenReturn(Mono.empty());

        Mono<Status> result = repositoryAdapter.findByName("Desconocido");

        StepVerifier.create(result)
                .verifyComplete();
    }
}
