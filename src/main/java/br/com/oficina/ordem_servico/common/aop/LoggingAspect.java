package br.com.oficina.ordem_servico.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

import org.springframework.context.annotation.Profile;

@Aspect
@Component
@Profile("!test")
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getMethod().getName();
        Object[] args = joinPoint.getArgs();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            logger.info("Requisição Controller: {}.{}() | URI: {} {} | Args: {}",
                    className, methodName, request.getMethod(), request.getRequestURI(), 
                    args != null ? Arrays.toString(args) : "null");
        } else {
            logger.info("Requisição Controller: {}.{}() | Args: {}", 
                    className, methodName, args != null ? Arrays.toString(args) : "null");
        }

        Object result = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - startTime;

        logger.info("Resposta Controller: {}.{}() | Tempo de Execução: {}ms | Resultado: {}",
                className, methodName, elapsedTime, result != null ? result : "void/null");

        return result;
    }

    @Around("within(@org.springframework.stereotype.Service *)")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getMethod().getName();
        Object[] args = joinPoint.getArgs();

        logger.info("Execução Service: {}.{}() | Args: {}", 
                className, methodName, args != null ? Arrays.toString(args) : "null");

        Object result = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - startTime;

        logger.info("Resultado Service: {}.{}() | Tempo de Execução: {}ms | Resultado: {}",
                className, methodName, elapsedTime, result != null ? result : "void/null");

        return result;
    }

    @Around("within(@org.springframework.stereotype.Repository *)")
    public Object logPersistence(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType() != null ? signature.getDeclaringType().getSimpleName() : "UnknownRepository";
        String methodName = signature.getMethod().getName();
        Object[] args = joinPoint.getArgs();

        logger.info("Acesso à Persistência: {}.{}() | Args: {}", 
                className, methodName, args != null ? Arrays.toString(args) : "null");

        Object result = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - startTime;

        logger.info("Retorno da Persistência: {}.{}() | Tempo de Execução: {}ms",
                className, methodName, elapsedTime);

        return result;
    }
}
