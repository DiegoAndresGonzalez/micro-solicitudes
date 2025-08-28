package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.RequestEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RequestReactiveRepository extends ReactiveCrudRepository<RequestEntity, Long>,
        ReactiveQueryByExampleExecutor<RequestEntity> {

}
