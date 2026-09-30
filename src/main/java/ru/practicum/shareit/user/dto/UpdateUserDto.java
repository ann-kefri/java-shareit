package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserDto {
    private String name;

    @Email(message = "Некорректный email")
    private String email;
}