package com.example.spacelab.model.literature;

import com.example.spacelab.model.course.Course;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="literature")
@NoArgsConstructor
public class Literature {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String author;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(value = EnumType.STRING)
    private LiteratureType type;

    private String keywords;

    @Column(length = 1000)
    private String description;

    private String resource_link;  // filename for files, URL for links

    private String thumbnail;

    public Literature(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    private Boolean is_verified = false;
}
