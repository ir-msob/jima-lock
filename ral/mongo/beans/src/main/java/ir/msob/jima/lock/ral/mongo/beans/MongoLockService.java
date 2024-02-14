package ir.msob.jima.lock.ral.mongo.beans;

import ir.msob.jima.lock.commons.BaseLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.locks.Lock;

@Service
@RequiredArgsConstructor
public class MongoLockService implements BaseLockService<String> {

    private final LockLogService lockLogService;

    @Override
    public <L extends Lock> L getLock(String... keys) {
        return (L) new MongoLock(lockLogService, keys);
    }

    @Override
    public <L extends Lock> L getLock(Collection<String> keys) {
        return (L) new MongoLock(lockLogService, keys);
    }

}
