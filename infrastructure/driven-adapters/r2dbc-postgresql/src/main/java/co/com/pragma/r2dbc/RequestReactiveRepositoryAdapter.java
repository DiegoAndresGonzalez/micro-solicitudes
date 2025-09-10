package co.com.pragma.r2dbc;

import co.com.pragma.model.request.Request;
import co.com.pragma.model.request.gateways.RequestRepository;
import co.com.pragma.r2dbc.entity.RequestEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@Transactional
public class RequestReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Request,
        RequestEntity,
    Long,
        RequestReactiveRepository
> implements RequestRepository {
    public RequestReactiveRepositoryAdapter(RequestReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Request.class));
    }

    @Override
    public Mono<Request> createRequest(Request request) {
        return super.save(request);
    }

    @Override
    public Flux<Request> findByStatusInOrLoanTypeInAndPaginated
            (List<Long> statusIds, List<Long> loanTypeIds, int page, int size) {
        int offset = page * size;
        return repository.findByStatusIdsOrLoanTypeIdsAndPaginated
                        (statusIds, loanTypeIds, size, offset)
                .map(this::toEntity);
    }

    @Override
    public Mono<Double> sumApprovedDebtsByEmail(String email) {
        return repository.sumApprovedDebtsByEmail(email);
    }

    @Override
    public Mono<Request> updateRequest(Request request) {
        return super.save(request);
    }

}
