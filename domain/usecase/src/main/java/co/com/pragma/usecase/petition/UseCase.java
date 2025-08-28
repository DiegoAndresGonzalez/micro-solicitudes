package co.com.pragma.usecase.petition;

import co.com.pragma.model.user.UserGateway;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.request.Request;
import co.com.pragma.model.request.gateways.RequestRepository;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.usecase.petition.exceptions.InvalidDataException;
import co.com.pragma.usecase.petition.exceptions.LoanTypeNotFoundException;
import co.com.pragma.usecase.petition.exceptions.StatusNotFoundException;
import co.com.pragma.usecase.petition.utils.Constants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UseCase {

    private final UserGateway userGateway;
    private final LoanTypeRepository loanTypeRepository;
    private final StatusRepository statusRepository;
    private final RequestRepository requestRepository;

    public Mono<Request> createRequest(Request request, String documentId) {
        validateRequestData(request, documentId);
        return validateUserExistence(request, documentId)
                .flatMap(validatedRequest ->
                        Mono.zip(
                                validateLoanType(validatedRequest),
                                validateStatus(validatedRequest)
                        ).map(tuple -> validatedRequest)
                )
                .flatMap(requestRepository::createRequest);
    }

    private void validateRequestData(Request request, String documentId) {
        if (request == null ||
                documentId == null || documentId.isBlank() ||
                request.getLoanTypeId() == null ||
                request.getLoanTerm() == null || request.getLoanTerm().isBlank() ||
                request.getAmount() == null || request.getAmount() <= Constants.NUMBER_ZERO) {
            throw new InvalidDataException(Constants.INVALID_DATA);
        }
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
}
