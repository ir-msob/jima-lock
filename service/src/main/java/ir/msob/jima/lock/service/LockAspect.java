package ir.msob.jima.lock.service;

import ir.msob.jima.core.beans.spel.SpelRepository;
import ir.msob.jima.core.commons.annotation.methodstats.MethodStats;
import ir.msob.jima.core.commons.logger.Logger;
import ir.msob.jima.core.commons.logger.LoggerFactory;
import ir.msob.jima.core.commons.util.BeanUtil;
import ir.msob.jima.lock.commons.BaseLockService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * AOP Aspect for locking functionality around annotated methods.
 * This aspect acquires a lock based on a provided key expression.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LockAspect {

    private static final Logger log = LoggerFactory.getLog(LockAspect.class);
    private final SpelRepository spelRepository;

    /**
     * Process the method execution with lock acquisition based on annotation.
     *
     * @param joinPoint ProceedingJoinPoint for the annotated method
     * @return the result of the annotated method execution
     * @throws Throwable if an error occurs during method execution
     */
    @MethodStats
    @Around("@annotation(ir.msob.jima.lock.commons.Lock)")
    public <K extends Serializable> Object processWithLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        Map<String, Object> methodParams = getMethodParameters(methodSignature, args);
        ir.msob.jima.lock.commons.Lock lockAnnotation = methodSignature.getMethod().getAnnotation(ir.msob.jima.lock.commons.Lock.class);
        Class<BaseLockService<K>> locksServiceClass = (Class<BaseLockService<K>>) lockAnnotation.lockService();
        BaseLockService<K> locksService = BeanUtil.getBean(locksServiceClass);

        String keyExpression = lockAnnotation.value();

        List<K> keys = new ArrayList<>();
        Object keysObject = spelRepository.execute(keyExpression, methodParams, Object.class);
        if (keysObject instanceof Collection<?> keyCollection) {
            keys.addAll(keyCollection.stream()
                    .map(o -> (K) o)
                    .toList());
        } else {
            keys.add((K) keysObject);
        }
        log.info("Attempting to acquire lock with keys: {}", keys);

        Lock lock = locksService.getLock(keys);
        lock.lock();

        try {
            log.debug("Lock acquired for keys: {}", keys);
            return joinPoint.proceed();
        } finally {
            lock.unlock();
            log.debug("Lock released for keys: {}", keys);
        }
    }

    /**
     * Extracts method parameters and their names.
     *
     * @param methodSignature MethodSignature instance for the method
     * @param args            Arguments passed to the method
     * @return Map containing method parameter names and their values
     */
    private Map<String, Object> getMethodParameters(MethodSignature methodSignature, Object[] args) {
        Map<String, Object> parameterMap = new LinkedHashMap<>();
        String[] parameterNames = methodSignature.getParameterNames();

        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                parameterMap.put(parameterNames[i], args[i]);
            }
        }

        return parameterMap;
    }
}

