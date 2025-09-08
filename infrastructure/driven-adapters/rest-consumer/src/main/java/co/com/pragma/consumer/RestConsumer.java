package co.com.pragma.consumer;

import co.com.pragma.consumer.mapper.UserMapper;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserGateway {

    private final WebClient client;
    private final UserMapper mapper;

    @Override
    public Mono<String> findUserByDocumentId(String documentId) {
        return client
                .get()
                .uri("http://auth-service:8080/api/v1/usuarios/{documentId}", documentId)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .map(UserResponse::getEmail);

    }

    @Override
    public Mono<User> findByEmail(String email) {
        return client
                .get()
                .uri("http://auth-service:8080/api/v1/usuarios/email/{email}", email)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .map(mapper::toDomain);
    }

}
