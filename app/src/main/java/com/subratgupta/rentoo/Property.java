package com.subratgupta.rentoo;

import java.util.List;

public class Property {

    private String name;
    private String address;
    private String type_of_space;
    private String rent;
    private String phone;
    private String tenant_type;
    private String email;
    private String pi41;
    private String pi42;
    private String pi43;
    private String pi44;
    private String pi45;
    private String ppi410;
    private String naming;
    private String city;
    private String local;


    public Property() {
    }

    /*public Property(String name, String address, String type_of_space, String rent) {
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
*/
    public String getName() {
        return name;
    }
    public String getAddress() {return address;}
    public String getType_of_space() {
        return type_of_space;
    }
    public String getPhone() {
        return phone;
    }
    public String getTenant_type() {
        return tenant_type;
    }
    public String getEmail() {
        return email;
    }
    public String getPi41() {
        return pi41;
    }
    public String getPi42() {
        return pi42;
    }
    public String getPi43() {
        return pi43;
    }
    public String getPi44(){
        return pi44;
    }
    public String getPi45() {
        return pi45;
    }
    public String getPpi410() {
        return ppi410;
    }
    public String getRent() {
        return rent;
    }
    public String getCity() {
        return city;
    }
    public String getLocal() {
        return local;
    }


}