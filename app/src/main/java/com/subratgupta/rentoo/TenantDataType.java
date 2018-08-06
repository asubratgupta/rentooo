package com.subratgupta.rentoo;

public class TenantDataType {
    private String name;
    private String city;
    private String local;
    private String phone;

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

    public String getCity() {
        return city;
    }

    public String getLocal() {
        return local;
    }

    public String getPhone() {
        return phone;
    }
}
