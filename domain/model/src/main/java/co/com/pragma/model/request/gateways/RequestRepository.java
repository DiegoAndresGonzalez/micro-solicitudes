package co.com.pragma.model.request.gateways;

import co.com.pragma.model.request.Request;
import reactor.core.publisher.Mono;

public interface RequestRepository {

    Mono<Request> createRequest(Request request);

}
