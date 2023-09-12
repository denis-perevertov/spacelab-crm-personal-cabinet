package com.example.spacelab.dto.contact;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ContactInfoEditDTO {

    private Long id;
    @Schema(example = "1")
    private Long adminID;
    @Schema(example = "+380123456789")
    private String phone;
    @Schema(example = "testemail@gmail.com")
    private String email;
    @Schema(example = "@test")
    private String telegram;

}
