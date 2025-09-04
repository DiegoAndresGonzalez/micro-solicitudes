package co.com.pragma.usecase.petition;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.requestforreview.RequestForReview;
import co.com.pragma.model.status.Status;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserGateway;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.request.Request;
import co.com.pragma.model.request.gateways.RequestRepository;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.usecase.petition.exceptions.InvalidDataException;
import co.com.pragma.usecase.petition.exceptions.LoanTypeNotFoundException;
import co.com.pragma.usecase.petition.exceptions.RequestException;
import co.com.pragma.usecase.petition.exceptions.StatusNotFoundException;
import co.com.pragma.usecase.petition.utils.Constants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@RequiredArgsConstructor
public class UseCase {

    private final UserGateway userGateway;
    private final LoanTypeRepository loanTypeRepository;
    private final StatusRepository statusRepository;
    private final RequestRepository requestRepository;

    public Mono<Request> createRequest(Request request, String documentId, String emailFromToken) {
        validateRequestData(request, documentId);

        return validateUserExistence(request, documentId)
                .flatMap(reqWithEmail -> {
                    if (!reqWithEmail.getEmail().equals(emailFromToken)) {
                        return Mono.error(new InvalidDataException(Constants.INVALID_DATA));
                    }
                    return Mono.zip(
                                    validateLoanType(reqWithEmail),
                                    validateStatus(reqWithEmail)
                            )
                            .map(tuple -> reqWithEmail)
                            .flatMap(requestRepository::createRequest);
                });
    }

    public Flux<RequestForReview> listRequestsForReview(int page, int size) {
        return Mono.just(Tuples.of(page, size))
                .filter(tuple -> tuple.getT1() >= 0 && tuple.getT2() > 0)
                .switchIfEmpty(Mono.error(new InvalidDataException(Constants.INVALID_PAGINATION)))
                .flatMapMany(tuple -> {
                    Flux<Long> statusIdsFlux = getStatusIdsForReview();
                    Flux<Long> loanTypeIdsFlux = getNonAutomaticLoanTypeIds();

                    return Flux.combineLatest(
                                    statusIdsFlux.collectList(),
                                    loanTypeIdsFlux.collectList(),
                                    Tuples::of
                            )
                            .flatMap(idsTuple ->
                                    requestRepository.findByStatusInOrLoanTypeInAndPaginated(
                                                    idsTuple.getT1(),
                                                    idsTuple.getT2(),
                                                    tuple.getT1(),
                                                    tuple.getT2()
                                            )
                                            .flatMap(this::buildDomainAggregate)
                                            .onErrorResume(ex ->
                                                    Flux.error(new RequestException(Constants.FAILED_REQUEST))
                                            )
                            );
                });
    }


    private Mono<Void> validateRequestData(Request request, String documentId) {
        if (request == null ||
                documentId == null || documentId.isBlank() ||
                request.getLoanTypeId() == null ||
                request.getLoanTerm() == null || request.getLoanTerm().isBlank() ||
                request.getAmount() == null || request.getAmount() <= Constants.NUMBER_ZERO) {
            return Mono.error(new InvalidDataException(Constants.INVALID_DATA));
        }
        return Mono.empty();
    }


    private Mono<Request> validateUserExistence(Request request, String documentId) {
        return userGateway.findUserByDocumentId(documentId)
                .map(email -> {
                    request.setEmail(email);
                    return request;
                });
    }

    private Mono<Request> validateLoanType(Request request) {
        return loanTypeRepository.findById(request.getLoanTypeId())
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(Constants.LOAN_TYPE_NOT_FOUND)))
                .map(loanType -> {
                    if (request.getAmount() < loanType.getMinAmount()) {
                        throw new InvalidDataException(Constants.MIN_LOAN_ERROR + loanType.getMinAmount());
                    }
                    if (request.getAmount() > loanType.getMaxAmount()) {
                        throw new InvalidDataException(Constants.MAX_LOAN_ERROR + loanType.getMaxAmount());
                    }
                    return request;
                });
    }
    private Mono<Request> validateStatus(Request request) {
        return statusRepository.findByName(Constants.SET_STATUS_FIRST)
                .switchIfEmpty(Mono.error(new StatusNotFoundException(Constants.STATUS_NOT_FOUND)))
                .map(status -> {
                    request.setStatusId(status.getId());
                    return request;
                });
    }


    private Flux<Long> getStatusIdsForReview() {
        return Flux.just(
                        Constants.SET_STATUS_FIRST,
                        Constants.SET_STATUS_REFUSED
                )
                .flatMap(statusRepository::findByName)
                .map(Status::getId);
    }

    private Flux<Long> getNonAutomaticLoanTypeIds() {
        return loanTypeRepository.findByAutomaticValidation(false)
                .map(LoanType::getId);
    }

    private Mono<RequestForReview> buildDomainAggregate(Request request) {
        Mono<User> userMono = userGateway.findByEmail(request.getEmail());
        Mono<LoanType> loanTypeMono = loanTypeRepository.findById(request.getLoanTypeId());
        Mono<Status> statusMono = statusRepository.findById(request.getStatusId());
        Mono<Double> debtMono = requestRepository.sumApprovedDebtsByEmail(request.getEmail());

        return Mono.zip(userMono, loanTypeMono, statusMono, debtMono)
                .map(tuple -> RequestForReview.builder()
                        .request(request)
                        .user(tuple.getT1())
                        .loanType(tuple.getT2())
                        .status(tuple.getT3())
                        .totalApprovedDebt(tuple.getT4())
                        .build());
    }
}

