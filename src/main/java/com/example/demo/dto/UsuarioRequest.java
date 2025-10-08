/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto;

import jakarta.validation.constraints.*;

public class UsuarioRequest {

    @NotBlank
    @Size(min = 4, max = 150)
    private String name;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "invalid_format")
    private String username;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "invalid_format")
    private String password;

    @Email(message = "invalid_format")
    private String email;

    @Pattern(regexp = "^[0-9]{10,14}$", message = "invalid_format")
    private String phone;
    
    @Pattern (regexp = "^[a-zA-Z0-9_]{10,600}$")
    private String experience;
    
    @Pattern (regexp = "^[a-zA-Z0-9_]{10,600}$")
    private String education;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getExperience() {return experience;}
    public void setExperience(String experience) {this.experience = experience;}
    
    public String getEducation() {return experience;}
    public void setEducation(String experience) {this.education = education;}
}
