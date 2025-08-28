package co.com.pragma.consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestConsumerTest {

    @Mock
    private WebClient client;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private RestConsumer restConsumer;

    @Test
    void shouldFindUserByDocumentIdSuccessfully() {

        UserResponse mockUserResponse = new UserResponse();
        mockUserResponse.setEmail("testuser@example.com");

        when(client.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(mockUserResponse));

        Mono<String> result = restConsumer.findUserByDocumentId("12345");

        StepVerifier.create(result)
                .expectNext("testuser@example.com")
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyMonoWhenUserNotFound() {

        when(client.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.empty());

        Mono<String> result = restConsumer.findUserByDocumentId("67890");

        StepVerifier.create(result)
                .verifyComplete();
    }
}
