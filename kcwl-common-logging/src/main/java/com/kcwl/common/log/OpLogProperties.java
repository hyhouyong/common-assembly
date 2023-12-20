package com.kcwl.common.log;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author ckwl
 */
@Data
@Component
@ConfigurationProperties(prefix = "kcwl.common.oplog")
public class OpLogProperties {
    String sensitiveMask="***";
    boolean checkSensitiveField = true;
    CopyOnWriteArraySet<String> sensitiveFields;

    public boolean includeSensitiveField(String fieldName) {
        if ( (sensitiveFields != null) && (fieldName != null) ) {
            for (String sensitiveField : sensitiveFields) {
                if ( fieldName.contains(sensitiveField) ) {
                    return true;
                }
            }
        }
        return false;
    }
}
