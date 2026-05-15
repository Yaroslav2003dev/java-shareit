package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemOwnerDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public RequestDto addRequest(RequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("В заголовке передан не существующий userId"));
        Request request = RequestMapper.toRequest(requestDto, user);
        log.info("Добавление запроса на вещь пользователем с id = " + userId);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<RequestItemDto> getMyRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("В заголовке передан не существующий userId"));
        List<Request> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);

        Map<Long, Request> requestsMap = requests.stream()
                .collect(Collectors.toMap(Request::getId, Function.identity()));

        List<Item> itemList = itemRepository.findAllByRequestId(requestsMap.keySet());

        Map<Long, List<Item>> itemMap = itemList.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        log.info("Вывод всех запросов на вещи от пользователя с id = " + userId);
        return requestsMap.values()
                .stream()
                .map(request -> {
                    List<ItemOwnerDto> itemOwnerDtoList = RequestMapper.toItemOwnerDtoList(itemMap.getOrDefault(request.getId(), Collections.emptyList()));
                    return RequestMapper.toRequestItemDto(request, itemOwnerDtoList);
                }).toList();
    }

    @Override
    public List<RequestDto> getAllRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("В заголовке передан не существующий userId"));
        List<Request> requests = requestRepository.findAllOtherUsersRequests(userId);
        log.info("Вывод списка актуальных запросов на вещи. Размер списка = " + requests.size());
        return requests.stream().map(RequestMapper::toRequestDto).toList();
    }

    @Override
    public RequestItemDto getByRequestId(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос c id " + requestId + " отсутствует"));
        List<Item> itemList = itemRepository.findAllByRequestId(Set.of(requestId));
        List<ItemOwnerDto> itemOwnerDtoList = RequestMapper.toItemOwnerDtoList(itemList);
        log.info("Получение запроса на вещь по id = " + requestId);
        return RequestMapper.toRequestItemDto(request, itemOwnerDtoList);
    }


}
