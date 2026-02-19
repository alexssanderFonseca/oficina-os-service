package br.com.oficina.ordem_servico;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import org.springframework.cloud.openfeign.EnableFeignClients;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
@EnableAspectJAutoProxy
@EnableScheduling
public class OrdemServicoApplication {

    private static final Logger logger = LoggerFactory.getLogger(OrdemServicoApplication.class);

    public static void main(String[] args) {
        logger.info("Aplicação iniciada com sucesso.");
        SpringApplication.run(OrdemServicoApplication.class, args);
    }

}
