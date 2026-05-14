package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto create(NewUserRequest newUserRequest) {
        User user = UserMapper.toUser(newUserRequest);
        validationEmail(newUserRequest.getEmail());
        userRepository.save(user);
        log.info("Создан пользователь с id = " + user.getId());
        return UserMapper.toUserDto(user);
    }

    public UserDto getById(Long id) {
        log.info("Поиск пользователя с id = " + id);
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public UserDto update(Long id, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        validationEmail(updateUserRequest.getEmail());
        UserMapper.updateUserFields(user, updateUserRequest);
        log.info("Пользователь с id = " + id + " обновлён");
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public UserDto delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userRepository.delete(user);
        log.info("Удалили пользователя с id = " + id);
        return UserMapper.toUserDto(user);
    }

    public void validationEmail(String newEmail) {
        boolean isExistsEmail = userRepository.existsByEmail(newEmail);
        if (isExistsEmail) {
            throw new InternalServerException("Пользователь с такой почтой уже существует");
        }
    }
}
