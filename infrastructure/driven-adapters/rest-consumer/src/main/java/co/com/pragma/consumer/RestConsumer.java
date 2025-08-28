package co.com.pragma.consumer;

import co.com.pragma.model.user.UserGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserGateway {
    private final WebClient client;

    @Override
    public Mono<String> findUserByDocumentId(String documentId) {
        return client
                .get()
                .uri("http://localhost:8080/api/v1/usuarios/{documentId}", documentId)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .map(UserResponse::getEmail);

    }
}
