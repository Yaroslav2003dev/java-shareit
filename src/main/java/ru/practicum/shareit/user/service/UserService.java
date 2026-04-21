package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    public UserDto create(NewUserRequest newUserRequest);

    public UserDto getById(Long id);

    public UserDto update(Long id, UpdateUserRequest updateUserRequest);

    public UserDto delete(Long id);

    public void validationEmail(String newEmail);
}
