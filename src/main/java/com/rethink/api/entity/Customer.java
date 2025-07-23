package com.rethink.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer extends PanacheEntity {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100)
    public String name;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(nullable = false, unique = true, length = 150)
    public String email;
    
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}", 
             message = "Telefone deve estar no formato (XX) XXXXX-XXXX ou (XX) XXXX-XXXX")
    @Column(nullable = false, length = 20)
    public String phone;
    
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}", 
             message = "CPF deve estar no formato XXX.XXX.XXX-XX")
    @Column(nullable = false, unique = true, length = 14)
    public String cpf;
    
    @Column(length = 200)
    public String address;
    
    @Column(length = 100)
    public String city;
    
    @Column(length = 2)
    @Pattern(regexp = "[A-Z]{2}", message = "Estado deve ter 2 letras maiúsculas")
    public String state;
    
    @Column(length = 9)
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve estar no formato XXXXX-XXX")
    public String zipCode;
    
    @Column(nullable = false)
    public LocalDateTime createdAt;
    
    @Column
    public LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "customer")
    public List<Order> orders = new ArrayList<>();
    
    public Customer() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Customer(String name, String email, String phone, String cpf) {
        this();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.cpf = cpf;
    }
}