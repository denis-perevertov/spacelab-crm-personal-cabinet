package com.example.spacelab.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
@Data
public class FilterForm {

    private Long id;
//    @Schema(defaultValue = "Test")
    private String name;
//    @Schema(defaultValue = "0")
    private Long course;
    private Long student;
//    @Schema(defaultValue = "test@gmail.com")
    private String email;
//    @Schema(defaultValue = "+380123456789")
    private String phone;
//    @Schema(defaultValue = "@test")
    private String telegram;
//    @Schema(defaultValue = "0")
    private Integer rating;
//    @Schema(defaultValue = "TEST")
    private String status;
    private String level;
//    @Schema(defaultValue = "Test")
    private String type;
//    @Schema(defaultValue = "Test")
    private String keywords;
//    @Schema(defaultValue = "0")
    private Long role;
    private String begin;
    private String end;
    private String date;
//    @Schema(defaultValue = "0", description = "Mentor's ID")
    private Long mentor;
//    @Schema(defaultValue = "0", description = "Manager's ID")
    private Long manager;
    private String combined;
    private Boolean active;

    private String nameAndAuthor;
    private Boolean verified;

    public static FilterBuilder with() { return new FilterBuilder(); }

    public FilterForm trim() {
        try {
            for(Field field : this.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if(field.getType().equals(String.class)) {
                    field.set(this, field.get(this) != null ? field.get(this).toString().trim() : null);
                }
                field.setAccessible(false);
            }
        } catch (Exception e) {
            log.error("Could not trim filters");
            log.error(e.getMessage());
        }

        return this;
    }

}
