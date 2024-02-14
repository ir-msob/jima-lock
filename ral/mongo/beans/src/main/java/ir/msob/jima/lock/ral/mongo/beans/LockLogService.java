package ir.msob.jima.lock.ral.mongo.beans;

import ir.msob.jima.core.ral.mongo.commons.criteria.MongoCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.ExecutionException;


@Repository
@RequiredArgsConstructor
public class LockLogService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Retryable(retryFor = {Exception.class}, maxAttemptsExpression = "${msob.core.mongodb.lock.retry.max-attempts:30}", backoff = @Backoff(
            delayExpression = "${msob.core.mongodb.lock.retry.delay:500}",
            maxDelayExpression = "${msob.core.mongodb.lock.retry.max-delay:1000}",
            multiplierExpression = "${msob.core.mongodb.lock.retry.multiplier:0.1}",
            randomExpression = "${msob.core.mongodb.lock.retry.random:false}"
    ))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Collection<LockLog> saveMany(Collection<LockLog> lockLogs) throws ExecutionException, InterruptedException {
        Instant now = Instant.now();
        lockLogs.forEach(documentLock -> documentLock.setTs(now));
        return reactiveMongoTemplate.insertAll(lockLogs)
                .collectList()
                .toFuture()
                .get();
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean deleteMany(Collection<String> ids) throws ExecutionException, InterruptedException {
        Query query = new Query();
        query.addCriteria(MongoCriteria.in(LockLog.FN.id, ids));
        return reactiveMongoTemplate.remove(query, LockLog.class)
                .map(deleteResult -> deleteResult.getDeletedCount() > 0)
                .toFuture()
                .get();
    }
}
