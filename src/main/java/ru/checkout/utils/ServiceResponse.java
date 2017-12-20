package ru.checkout.utils;

public class ServiceResponse {
    private Object data;
    private Metadata metadata;

    public ServiceResponse(Object data, Metadata metadata) {
        this.data = data;
        this.metadata = metadata;
    }

    public Object getData() {
        return data;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public static class Metadata {
        private final int totalCount;

        public Metadata(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getTotalCount() {
            return totalCount;
        }
    }
}
