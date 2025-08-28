package co.com.pragma.r2dbc;

import co.com.pragma.model.request.Request;
import co.com.pragma.model.request.gateways.RequestRepository;
import co.com.pragma.r2dbc.entity.RequestEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
@Transactional
public class RequestReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Request,
        RequestEntity,
    Long,
        RequestReactiveRepository
> implements RequestRepository {
    public RequestReactiveRepositoryAdapter(RequestReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Request.class));
    }

    @Override
    public Mono<Request> createRequest(Request request) {
        return super.save(request);
    }
}
