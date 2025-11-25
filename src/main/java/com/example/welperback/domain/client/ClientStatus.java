package com.example.welperback.domain.client;

import java.util.Arrays;

public enum ClientStatus {
    CONSULTING("상담중"),
    IN_PROGRESS("솔루션 진행 중"),
    COMPLETED("종료");

    private final String databaseValue;

    ClientStatus(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String getDatabaseValue() {
        return databaseValue;
    }

    public static ClientStatus fromDatabaseValue(String value) {
        return Arrays.stream(values())
                .filter(status -> status.databaseValue.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown client status: " + value));
    }
}
