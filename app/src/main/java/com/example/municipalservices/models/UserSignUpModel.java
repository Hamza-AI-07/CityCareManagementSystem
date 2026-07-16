package com.example.municipalservices.models;

public class UserSignUpModel {

    String name;
    String email;
    String password;
    String imageURL;
    String role;


    public UserSignUpModel(String name, String email, String password) {
        this(name, email, password, null, "User");
    }


    public UserSignUpModel(String name, String email, String password, String imageURL) {
        this(name, email, password, imageURL, "User");
    }

    public UserSignUpModel(String name, String email, String password, String imageURL, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.imageURL = imageURL;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    @Override
    public String toString()
    {
        return "\n" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '\n';
    }

    public UserSignUpModel()
    {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
