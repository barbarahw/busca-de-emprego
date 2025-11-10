/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto;

import jakarta.validation.constraints.*;

public class EmpresaRequest {
    @NotBlank(message = "invalid_format")
    @Size(min = 4, max = 150, message = "invalid_format")
    private String name;
    
    @NotBlank(message = "invalid_format")
    @Size(min = 4, max = 150, message = "invalid_format")
    private String business;
    
    @NotBlank(message = "invalid_format")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,20}$", message = "invalid_format")
    private String username;
    
    @NotBlank(message = "invalid_format")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,20}$", message = "invalid_format")
    private String password;
    
    @NotBlank(message = "invalid_format")
    @Size(min = 3, max = 150, message = "invalid_format")
    private String street;
    
    @NotBlank(message = "invalid_format")
    @Pattern(regexp = "^[1-9][0-9]{0,7}$", message = "invalid_format")
    private String number;
    
    @NotBlank(message = "invalid_format")
    @Size(min = 3, max = 150, message = "invalid_format")
    private String city;
    
    @NotBlank(message = "invalid_format")
    @Pattern(regexp = "^[A-Z]{2}$", message = "invalid_format")
    private String state;
    
    @NotBlank(message = "invalid_format")
    @Pattern(regexp = "^\\d{10,14}$", message = "invalid_format")
    private String phone;
    
    @NotBlank(message = "invalid_format")
    @Email(message = "invalid_format")
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
}
