package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserDto create(NewUserRequest newUserRequest) {
        User user = UserMapper.toUser(newUserRequest);
        validationEmail(newUserRequest.getEmail());
        Long id = userRepository.save(user);
        user.setId(id);
        user = userRepository.getById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("Создан пользователь с id = " + id);
        return UserMapper.toUserDto(user);
    }

    public UserDto getById(Long id) {
        log.info("Поиск пользователя с id = " + id);
        User user = userRepository.getById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return UserMapper.toUserDto(user);
    }

    public UserDto update(Long id, UpdateUserRequest updateUserRequest) {
        User user = userRepository.getById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        validationEmail(updateUserRequest.getEmail());
        User userUp = UserMapper.updateUserFields(user, updateUserRequest);
        userRepository.update(id, userUp);
        log.info("Пользователь с id = " + id + " обновлён");
        return UserMapper.toUserDto(userRepository.getById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден после обновления данных")));
    }

    public UserDto delete(Long id) {
        userRepository.getById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("Удалили пользователя с id = " + id);
        return UserMapper.toUserDto(userRepository.delete(id));
    }

    public void validationEmail(String newEmail) {
        long count = userRepository.getEmailUsers().stream()
                .filter(email -> email != null && email.equals(newEmail))
                .count();
        if (count > 0) {
            throw new InternalServerException("Пользователь с такой почтой уже существует");
        }
    }
}
