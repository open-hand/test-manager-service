package io.choerodon.test.manager.infra.enums;

public enum TestApiAssertionTypeEnum {
    RESPONSE_ASSERTION("response"),
    JSON_ASSERTION("json"),
    DURATION_ASSERTION("duration"),
    SIZE_ASSERTION("size");


    private String type;


    TestApiAssertionTypeEnum(String type) {
        this.type = type;
    }

    public String value() {
        return this.type;
    }
}
