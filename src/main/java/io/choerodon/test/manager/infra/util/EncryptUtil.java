package io.choerodon.test.manager.infra.util;

import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.starter.keyencrypt.core.EncryptionService;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author superlee
 * @since 2020-07-15
 */
@Component
public class EncryptUtil {

    private EncryptionService encryptionService;

    public EncryptUtil(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public void decryptIfFieldEncrypted(Field field, Object objectUpdate, Object v) throws IllegalAccessException {
        Encrypt encrypt = field.getAnnotation(Encrypt.class);
        if (encrypt != null && v instanceof String) {
            String decrypt = encryptionService.decrypt(v.toString(), encrypt.value());
            field.set(objectUpdate, Long.parseLong(decrypt));
        } else {
            field.set(objectUpdate, v == null ? null : Long.valueOf(v.toString()));
        }
    }
}
