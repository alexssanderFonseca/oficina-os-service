package br.com.alexsdm.postech.oficina.ordem_servico;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
@EnableAspectJAutoProxy
public class OrdemServicoApplication {

    private static final Logger logger = LoggerFactory.getLogger(OrdemServicoApplication.class);

    public static void main(String[] args) {
        logger.info("Aplicação iniciada com sucesso.");
        SpringApplication.run(OrdemServicoApplication.class, args);
    }

}
