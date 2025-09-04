package co.com.pragma.model.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void builder_shouldCreateUser() {
        User user = User.builder()
                .documentId("123456")
                .fullName("Diego González")
                .email("diego@email.com")
                .baseSalary(3000.0)
                .build();

        assertThat(user.getDocumentId()).isEqualTo("123456");
        assertThat(user.getFullName()).isEqualTo("Diego González");
        assertThat(user.getEmail()).isEqualTo("diego@email.com");
        assertThat(user.getBaseSalary()).isEqualTo(3000.0);
    }

    @Test
    void settersAndGetters_shouldUpdateFields() {
        User user = new User();
        user.setDocumentId("654321");
        user.setFullName("Otro Nombre");
        user.setEmail("nuevo@email.com");
        user.setBaseSalary(2000.0);

        assertThat(user.getDocumentId()).isEqualTo("654321");
        assertThat(user.getFullName()).isEqualTo("Otro Nombre");
        assertThat(user.getEmail()).isEqualTo("nuevo@email.com");
        assertThat(user.getBaseSalary()).isEqualTo(2000.0);
    }

    @Test
    void toBuilder_shouldCloneAndModify() {
        User user = User.builder()
                .documentId("123456")
                .fullName("Diego González")
                .email("diego@email.com")
                .baseSalary(3000.0)
                .build();

        User copy = user.toBuilder()
                .email("modificado@email.com")
                .build();

        assertThat(copy.getEmail()).isEqualTo("modificado@email.com");
        assertThat(copy.getFullName()).isEqualTo("Diego González");
    }
}
