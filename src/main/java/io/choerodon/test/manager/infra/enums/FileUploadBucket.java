package io.choerodon.test.manager.infra.enums;

/**
 * @author superlee
 * @since 2022-03-15
 */
public enum FileUploadBucket {

    TEST_BUCKET("test-service");

    private String bucket;

    FileUploadBucket(String bucket) {
        this.bucket = bucket;
    }

    public String bucket() {
        return this.bucket;
    }
}
