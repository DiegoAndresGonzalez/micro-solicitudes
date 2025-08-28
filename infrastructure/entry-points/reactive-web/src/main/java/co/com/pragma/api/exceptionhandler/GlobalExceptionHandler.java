package co.com.pragma.api.exceptionhandler;

import co.com.pragma.api.exceptions.InvalidConsumeException;
import co.com.pragma.api.utils.Constants;
import co.com.pragma.usecase.petition.exceptions.InvalidDataException;
import co.com.pragma.usecase.petition.exceptions.LoanTypeNotFoundException;
import co.com.pragma.usecase.petition.exceptions.StatusNotFoundException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import java.util.Map;

@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  WebProperties.Resources resources,
                                  ApplicationContext applicationContext) {
        super(errorAttributes, resources, applicationContext);
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private final Map<Class<? extends Throwable>, HttpStatus> exceptionToStatus = Map.of(
            InvalidDataException.class, HttpStatus.BAD_REQUEST,
            LoanTypeNotFoundException.class, HttpStatus.BAD_REQUEST,
            StatusNotFoundException.class, HttpStatus.BAD_REQUEST,
            InvalidConsumeException.class, HttpStatus.BAD_REQUEST,
            WebClientResponseException.BadRequest.class, HttpStatus.BAD_REQUEST
    );

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);

        HttpStatus status = exceptionToStatus.getOrDefault(error.getClass(),
                HttpStatus.INTERNAL_SERVER_ERROR);

        String message = (status == HttpStatus.INTERNAL_SERVER_ERROR)
                ? Constants.INTERNAL_SERVER_ERROR
                : error.getMessage();

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(Constants.MESSAGE, message));
    }
}