package co.com.pragma.model.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RequestTest {

    @Test
    void testRequestAllArgsConstructorAndGetters() {
        Request request = new Request(1L, 50000, "user@test.com", "24 months", 1L, 2L);

        assertEquals(1L, request.getId());
        assertEquals(50000, request.getAmount());
        assertEquals("user@test.com", request.getEmail());
        assertEquals("24 months", request.getLoanTerm());
        assertEquals(1L, request.getStatusId());
        assertEquals(2L, request.getLoanTypeId());
    }

    @Test
    void testRequestNoArgsConstructorAndSetters() {
        Request request = new Request();
        request.setId(2L);
        request.setAmount(10000);
        request.setEmail("anotheruser@test.com");
        request.setLoanTerm("12 months");
        request.setStatusId(3L);
        request.setLoanTypeId(4L);

        assertEquals(2L, request.getId());
        assertEquals(10000, request.getAmount());
        assertEquals("anotheruser@test.com", request.getEmail());
        assertEquals("12 months", request.getLoanTerm());
        assertEquals(3L, request.getStatusId());
        assertEquals(4L, request.getLoanTypeId());
    }

    @Test
    void testRequestBuilder() {
        Request request = Request.builder()
                .id(3L)
                .amount(25000)
                .email("builderuser@test.com")
                .loanTerm("36 months")
                .statusId(5L)
                .loanTypeId(6L)
                .build();

        assertEquals(3L, request.getId());
        assertEquals(25000, request.getAmount());
        assertEquals("builderuser@test.com", request.getEmail());
        assertEquals("36 months", request.getLoanTerm());
        assertEquals(5L, request.getStatusId());
        assertEquals(6L, request.getLoanTypeId());
    }
}