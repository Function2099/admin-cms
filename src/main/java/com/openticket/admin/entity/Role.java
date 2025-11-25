package com.openticket.admin.entity;

public enum Role {
    ADMIN(0),
    COMPANY(1),
    USER(2);

    private final int code;

    Role(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Role fromCode(int code) {
        for (Role r : Role.values()) {
            if (r.code == code)
                return r;
        }
        throw new IllegalArgumentException("Invalid Role code: " + code);
    }
}
