package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(NewUserRequest newUserRequest) {
        User user = new User();
        user.setName(newUserRequest.getName());
        user.setEmail(newUserRequest.getEmail());
        return user;
    }

    public static User updateUserFields(User user, UpdateUserRequest updateUserRequest) {
        if (updateUserRequest.getName() != null && !updateUserRequest.getName().isBlank()) {
            user.setName(updateUserRequest.getName());
        }
        if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().isBlank()) {
            user.setEmail(updateUserRequest.getEmail());
        }
        return user;
    }
}
