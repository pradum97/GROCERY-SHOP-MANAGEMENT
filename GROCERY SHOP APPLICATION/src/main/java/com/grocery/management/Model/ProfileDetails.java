package com.grocery.management.Model;

import java.sql.Blob;

public class ProfileDetails {
    private int id;
    private long phone;
    private Blob user_image;
    private String full_name, email_id, role, date_of_birth,
            username, gender, address, create_time;

    public ProfileDetails(int id, long phone, Blob user_image, String full_name, String email_id,
                          String role, String date_of_birth, String username,
                          String gender, String address, String create_time) {
        this.id = id;
        this.phone = phone;
        this.user_image = user_image;
        this.full_name = full_name;
        this.email_id = email_id;
        this.role = role;
        this.date_of_birth = date_of_birth;
        this.username = username;
        this.gender = gender;
        this.address = address;
        this.create_time = create_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public Blob getUser_image() {
        return user_image;
    }

    public void setUser_image(Blob user_image) {
        this.user_image = user_image;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
