package ir.msob.jima.lock.ral.mongo.beans;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "msob.core.mongodb.lock")
@Setter
@Getter
@NoArgsConstructor
public class LockProperties {

    private Retry retry = new Retry();


    @Setter
    @Getter
    @NoArgsConstructor
    public class Retry {
        private long maxAttempts = 30L;
        private long delay = 500L;
        private long maxDelay = 1000L;
        private double multiplier = 0.1;
        private String delayExpression = "";
        private String maxDelayExpression = "";
        private String multiplierExpression = "";
        private boolean random = false;
        private String randomExpression = "";
    }
}
