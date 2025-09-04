package co.com.pragma.model.request.gateways;

import co.com.pragma.model.request.Request;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RequestRepository {

    Mono<Request> createRequest(Request request);
    Flux<Request> findByStatusInOrLoanTypeInAndPaginated(List<Long> statusIds, List<Long> loanTypeIds, int page, int size);
    Mono<Double> sumApprovedDebtsByEmail(String email);
}
