package ir.msob.jima.lock.ral.mongo.beans;

import ir.msob.jima.core.beans.properties.JimaProperties;
import ir.msob.jima.core.commons.logger.Logger;
import ir.msob.jima.core.commons.logger.LoggerFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class LockLogConfiguration {

    private static final Logger log = LoggerFactory.getLog(LockLogConfiguration.class);

    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final JimaProperties jimaProperties;

    @PostConstruct
    public void createIndexes() {
        reactiveMongoTemplate.indexOps(LockLog.class)
                .ensureIndex(new Index().on(LockLog.FN.ts.name(), Sort.Direction.ASC).expire(jimaProperties.getLock().getExpiration()))
                .subscribe(s -> log.info("Result of creating index of {}.{} is {}", LockLog.DOMAIN_NAME, LockLog.FN.ts, s));
    }
}
