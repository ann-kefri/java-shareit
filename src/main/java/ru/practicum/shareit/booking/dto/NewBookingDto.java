package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class NewBookingDto {

    @NotNull(message = "Дата начала обязательна")
    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Дата конца обязательна")
    @Future(message = "Дата конца должна быть в будущем")
    private LocalDateTime end;

    @NotNull(message = "Идентификатор вещи обязателен")
    private Long itemId;
}