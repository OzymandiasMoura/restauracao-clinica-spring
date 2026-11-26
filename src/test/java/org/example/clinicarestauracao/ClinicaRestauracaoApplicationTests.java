package org.example.clinicarestauracao;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class ClinicaRestauracaoApplicationTests
{

    @DisplayName("Deve carregar o contexto da aplicação")
    @Test
    void contextLoads()
    {
    }

}
