package ir.msob.jima.lock.commons;

import ir.msob.jima.core.commons.util.GenericTypeUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.locks.Lock;

public interface BaseLockService<K extends Serializable> {

    default Class<K> getKeyClass() {
        return (Class<K>) GenericTypeUtil.resolveTypeArguments(getClass(), BaseLockService.class, 0);
    }


    <L extends Lock> L getLock(K... keys);

    <L extends Lock> L getLock(Collection<K> keys);
}
