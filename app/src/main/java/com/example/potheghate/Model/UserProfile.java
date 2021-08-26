package com.example.potheghate.Model;

public class UserProfile {
    private String address;
    private String name;
    private String email;
    private String phone;

    public UserProfile(String address, String name, String email, String phone) {
        this.address = address;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    public UserProfile(String address, String name, String email) {
        this.address = address;
        this.name = name;
        this.email = email;
    }


    public UserProfile(){ }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getname() {
        return name;
    }
    public String getemail() {
        return email;
    }

    public String getaddress() {
        return address;
    }


}
