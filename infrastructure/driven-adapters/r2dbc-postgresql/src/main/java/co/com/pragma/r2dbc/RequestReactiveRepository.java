package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.RequestEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RequestReactiveRepository extends ReactiveCrudRepository<RequestEntity, Long>,
        ReactiveQueryByExampleExecutor<RequestEntity> {

    @Query("SELECT * FROM request " +
            "WHERE status_id IN (:statusIds) OR loan_type_id IN (:loanTypeIds) " +
            "ORDER BY id DESC LIMIT :size OFFSET :offset")
    Flux<RequestEntity> findByStatusIdsOrLoanTypeIdsAndPaginated(List<Long> statusIds, List<Long> loanTypeIds, int size, int offset);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM request r JOIN status s ON r.status_id " +
            "= s.id WHERE r.email = $1 AND s.name = 'Aprobado'")
    Mono<Double> sumApprovedDebtsByEmail(String email);

}
