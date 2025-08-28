package co.com.pragma.model.user;

import reactor.core.publisher.Mono;

public interface UserGateway {
    Mono<String> findUserByDocumentId(String documentId);

}
