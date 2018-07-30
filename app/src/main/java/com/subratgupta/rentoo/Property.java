package com.subratgupta.rentoo;

public class Property {

    private String name;
    private String address;
    private String type_of_space;
    private String rent;
    private String id;

    public Property() {
    }

    public Property(String name, String address, String type_of_space, String rent) {
        this.name = name;
        this.address = address;
        this.type_of_space = type_of_space;
        this.rent = rent;
    }

    public Property(String address, String age, String email, String isComplete, String marital, String marital_int, String name, String occupation, String phone, String rent, String type_of_space) {
        this.name = name;
        this.address = address;
        this.type_of_space = type_of_space;
        this.rent = rent;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {return address;}

    public String getType_of_space() {
        return type_of_space;
    }

    public String getRent() {
        return rent;
    }
}
