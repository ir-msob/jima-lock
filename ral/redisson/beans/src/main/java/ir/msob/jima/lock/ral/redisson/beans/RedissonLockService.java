package ir.msob.jima.lock.ral.redisson.beans;

import ir.msob.jima.lock.commons.BaseLockService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;

@Service
@RequiredArgsConstructor
public class RedissonLockService implements BaseLockService<String> {
    private final RedissonClient redissonClient;


    @Override
    public <L extends Lock> L getLock(String... keys) {
        return getLock(List.of(keys));
    }

    @Override
    public <L extends Lock> L getLock(Collection<String> keys) {
        Collection<RLock> locks = keys.stream()
                .map(redissonClient::getLock)
                .toList();
        return (L) redissonClient.getMultiLock(locks.toArray(new RLock[locks.size()]));
    }


}
