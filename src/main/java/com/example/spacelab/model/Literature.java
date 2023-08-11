package com.example.spacelab.model;

import com.example.spacelab.util.LiteratureType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Literature {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String author;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private LiteratureType type;

    private List<String> keywords;  // or just String

    private String resource_link;  // filename for files, URL for links

    private Boolean is_verified;
}
