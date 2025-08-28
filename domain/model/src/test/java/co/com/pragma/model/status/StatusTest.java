package co.com.pragma.model.status;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusTest {

    @Test
    void testStatusAllArgsConstructorAndGetters() {
        Status status = new Status(1L, "Active", "Request is currently being processed.");

        assertEquals(1L, status.getId());
        assertEquals("Active", status.getName());
        assertEquals("Request is currently being processed.", status.getDescription());
    }

    @Test
    void testStatusNoArgsConstructorAndSetters() {
        Status status = new Status();
        status.setId(2L);
        status.setName("Rejected");
        status.setDescription("Request was denied due to invalid data.");

        assertEquals(2L, status.getId());
        assertEquals("Rejected", status.getName());
        assertEquals("Request was denied due to invalid data.", status.getDescription());
    }

    @Test
    void testStatusBuilder() {
        Status status = Status.builder()
                .id(3L)
                .name("Approved")
                .description("Request was approved and is ready for disbursement.")
                .build();

        assertEquals(3L, status.getId());
        assertEquals("Approved", status.getName());
        assertEquals("Request was approved and is ready for disbursement.", status.getDescription());
    }
}