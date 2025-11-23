package com.example.demo.dto;

public class VagaResponse {

    private Long job_id;
    private String title;
    private String area;
    private String company;
    private String description;
    private String state;
    private String city;
    private Double salary;
    private String contact;

    // Getters e setters
    public Long getJob_id() { return job_id; }
    public void setJob_id(Long job_id) { this.job_id = job_id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}
