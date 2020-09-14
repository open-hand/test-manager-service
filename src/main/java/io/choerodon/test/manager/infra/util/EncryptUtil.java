package io.choerodon.test.manager.infra.util;

import io.choerodon.test.manager.api.vo.agile.SearchDTO;
import org.apache.commons.lang3.StringUtils;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.EncryptionService;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

/**
 * @author superlee
 * @since 2020-07-15
 */
@Component
public class EncryptUtil {

    public static final String BLANK_KEY = "";

    private EncryptionService encryptionService;

    public EncryptUtil(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public void decryptIfFieldEncrypted(Field field, Object objectUpdate, Object v) throws IllegalAccessException {
        Encrypt encrypt = field.getAnnotation(Encrypt.class);
        if (encrypt != null && v instanceof String && EncryptContext.isEncrypt()) {
            String decrypt = encryptionService.decrypt(v.toString(), encrypt.value());
            field.set(objectUpdate, Long.parseLong(decrypt));
        } else {
            field.set(objectUpdate, v == null ? null : Long.valueOf(v.toString()));
        }
    }

    public void decryptSearchDTO(SearchDTO search) {
        if (!EncryptContext.isEncrypt()){
            return;
        }

        Optional<Map<String, Object>> searchArgs = Optional.ofNullable(search).map(SearchDTO::getSearchArgs);
        if (searchArgs.isPresent()) {
            decryptSa(search, searchArgs);
        }
    }

    @SuppressWarnings("unchecked")
    private void decryptSa(SearchDTO search, Optional<Map<String, Object>> saMapOptional) {
        String tempStr;
        // priorityId
        tempStr = saMapOptional.map(ad -> (String)(ad.get("priorityId"))).orElse(null);
        if (StringUtils.isNotBlank(tempStr)) {
            search.getSearchArgs().put("priorityId",encryptionService.decrypt(tempStr, BLANK_KEY));
        }
    }
}
