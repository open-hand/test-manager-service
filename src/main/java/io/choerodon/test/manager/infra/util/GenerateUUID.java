package io.choerodon.test.manager.infra.util;

import java.util.UUID;

public class GenerateUUID {

    private GenerateUUID() {
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
