package io.choerodon.test.manager.infra.common.utils;

import java.util.UUID;

public class GenerateUUID {

    private GenerateUUID() {
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
