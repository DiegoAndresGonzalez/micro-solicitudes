package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface UserGateway {
    Mono<String> findUserByDocumentId(String documentId);
    Mono<User> findByEmail(String email);

}
