package co.com.pragma.api;

import co.com.pragma.api.dto.LoanRequestDto;
import co.com.pragma.api.dto.LoanResponseDto;
import co.com.pragma.api.dto.RequestForReviewResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createLoanRequest",
                    operation = @Operation(
                            operationId = "createLoanRequest",
                            tags = {"Solicitudes de Prestamo"},
                            summary = "Crea una nueva solicitud de prestamo",
                            description = "Este endpoint permite a un usuario crear una solicitud de préstamo de dinero.",
                            requestBody = @RequestBody(
                                    description = "Datos para crear la solicitud de préstamo",
                                    required = true,
                                    content = @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = LoanRequestDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Solicitud de préstamo creada exitosamente",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = LoanResponseDto.class)
                                            )
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
                                    @ApiResponse(responseCode = "404", description = "Tipo de préstamo o usuario no encontrado"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "listRequestsForReview",
                    operation = @Operation(
                            operationId = "listRequestsForReview",
                            tags = {"Solicitudes de Prestamo"},
                            summary = "Lista solicitudes de préstamo para revision",
                            description = "Devuelve un listado paginado de solicitudes de prestamo en estado pendiente de revision.",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Listado obtenido correctamente",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = RequestForReviewResponseDto.class)
                                            )
                                    ),
                                    @ApiResponse(responseCode = "404", description = "No se encontraron solicitudes"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/solicitud"), handler::createLoanRequest)
                .andRoute(GET("/api/v1/solicitud"), handler::listRequestsForReview);
    }
}
