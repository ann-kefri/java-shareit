package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(NewUserDto dto);

    UserDto update(Long id, UpdateUserDto dto);

    UserDto getById(Long id);

    List<UserDto> getAll();

    void delete(Long id);
}