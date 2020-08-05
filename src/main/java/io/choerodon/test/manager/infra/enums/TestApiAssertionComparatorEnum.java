package io.choerodon.test.manager.infra.enums;

public enum TestApiAssertionComparatorEnum {
    EQUALS("equals"),
    NOT_EQUALS("notEquals"),
    GRATER_THAN("graterThan"),
    LESS_THEN("lessThan"),
    GRATER_THAN_OR_EQUAL_TO("graterThanOrEqualTo"),
    LESS_THAN_OR_EQUAL_TO("lessThanOrEqualTo"),
    CONTAINS("contains"),
    MATCHED("matched"),
    SUBSTRING("substring"),
    NOT("not"),
    OR("or"),
    REGEX("regex");

    private String type;

    TestApiAssertionComparatorEnum(String type) {
        this.type = type;
    }

    public String value() {
        return this.type;
    }
}
