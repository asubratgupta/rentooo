package com.subratgupta.rentoo;

public class TenantDataType {
    private String name;

    public TenantDataType() {
    }

    public TenantDataType(String name) {
        this.name = name;
    }

    public TenantDataType(String address, String age, String email, String isComplete, String marital, String marital_int, String name, String occupation, String phone, int rent, String type_of_space) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
