package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vagas")
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false)
    private String area;

    @Column(nullable = false, length = 5000)
    private String description;

    @Column(nullable = false)
    private String state;
    
    @Column (nullable = false)
    private String city;

    @Column(nullable = false)
    private String contact;

    private Double salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Empresa company;

    public Vaga() {}

    public Vaga(String title, String area, String description, String state, String city, String contact, Double salary, Empresa company) {
        this.title = title;
        this.area = area;
        this.description = description;
        this.state = state;
        this.city = city;
        this.contact = contact;
        this.salary = salary;
        this.company = company;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Empresa getCompany() {
        return company;
    }

    public void setCompany(Empresa company) {
        this.company = company;
    }
}
