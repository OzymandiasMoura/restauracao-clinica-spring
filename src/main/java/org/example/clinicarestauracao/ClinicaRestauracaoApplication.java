package org.example.clinicarestauracao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClinicaRestauracaoApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(ClinicaRestauracaoApplication.class, args);
    }

}
