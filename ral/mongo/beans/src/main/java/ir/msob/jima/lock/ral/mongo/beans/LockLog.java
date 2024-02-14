package ir.msob.jima.lock.ral.mongo.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import ir.msob.jima.core.commons.model.domain.BaseDomain;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = LockLog.DOMAIN_NAME)
public class LockLog implements BaseDomain<String> {
    @Transient
    public static final String DOMAIN_NAME = "LockLog";
    @Id
    private String id;
    private Instant ts;

    @Override
    public String getDomainId() {
        return getId();
    }

    @Override
    public void setDomainId(String id) {
        setId(id);
    }

    @Override
    public String getDomainIdName() {
        return FN.id.name();
    }

    public enum FN {
        id, ts
    }
}
