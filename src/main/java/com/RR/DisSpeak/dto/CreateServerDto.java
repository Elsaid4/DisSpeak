package com.RR.DisSpeak.dto;

import com.RR.DisSpeak.model.entity.User;
import lombok.Data;

@Data
public class CreateServerDto {
    private String name;
    private Long ownerId;
}
