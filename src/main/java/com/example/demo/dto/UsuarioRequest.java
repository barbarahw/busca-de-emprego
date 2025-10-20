package com.example.demo.dto;

import jakarta.validation.constraints.*;

public class UsuarioRequest {
    
    @NotBlank(message = "invalid_format")
    @Size(min = 4, max = 150, message = "invalid_format")
    private String name;
    
    @NotBlank(message = "invalid_format")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "invalid_format")
    @Size(min = 3, max = 20, message = "invalid_format")
    private String username;
    
    @NotBlank(message = "invalid_format")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "invalid_format")
    @Size(min = 3, max = 20, message = "invalid_format")
    private String password;
    
    @Email(message = "invalid_format")
    private String email;
    
    @Pattern(regexp = "^\\d{10,14}$", message = "invalid_format")
    private String phone;
    
    @Size(min = 10, max = 600, message = "invalid_format")
    private String experience;
    
    @Size(min = 10, max = 600, message = "invalid_format")
    private String education;

    // Getters e Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }
}