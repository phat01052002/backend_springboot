package com.example.demo.Entity;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Category")
public class    Category implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryId")
    private Long categoryId;

    @Column(name = "name", columnDefinition = "nvarchar(100) not null")
    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy="category",cascade = CascadeType.ALL)
    private Set<Product> product;

    private String image;
}