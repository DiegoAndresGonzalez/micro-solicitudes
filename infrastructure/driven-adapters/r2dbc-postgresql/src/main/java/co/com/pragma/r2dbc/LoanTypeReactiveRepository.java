package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.LoanTypeEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface LoanTypeReactiveRepository extends ReactiveCrudRepository<LoanTypeEntity, Long>,
        ReactiveQueryByExampleExecutor<LoanTypeEntity> {

    Flux<LoanTypeEntity> findByAutomaticValidation(Boolean automaticValidation);

}
