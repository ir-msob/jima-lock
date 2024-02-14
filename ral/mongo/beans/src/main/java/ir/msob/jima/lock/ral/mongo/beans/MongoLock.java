package ir.msob.jima.lock.ral.mongo.beans;

import lombok.SneakyThrows;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MongoLock implements Lock {
    private final LockLogService lockLogService;
    private final Collection<LockLog> lockLogs;

    public MongoLock(LockLogService lockLogService, String... ids) {
        this(lockLogService, List.of(ids));
    }

    public MongoLock(LockLogService lockLogService, Collection<String> ids) {
        this.lockLogService = lockLogService;
        this.lockLogs = ids.stream()
                .map(id -> LockLog.builder()
                        .id(id)
                        .build())
                .toList();
    }

    @SneakyThrows
    @Override
    public void lock() {
        lockLogService.saveMany(this.lockLogs);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @SneakyThrows
    @Override
    public void unlock() {
        lockLogService.deleteMany(this.lockLogs.stream().map(LockLog::getId).toList());
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
