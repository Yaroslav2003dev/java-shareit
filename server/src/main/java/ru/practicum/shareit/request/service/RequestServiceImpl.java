package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
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
        return requests.stream().map(RequestMapper::toRequestDto).toList();
    }

    @Override
    public RequestItemDto getByRequestId(Long requestId) {
        userRepository.findById(requestId).orElseThrow(() -> new NotFoundException("В заголовке передан не существующий userId"));
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос c id " + requestId + " отсутствует"));
        List<Item> itemList = itemRepository.findAllByRequestId(Set.of(requestId));
        List<ItemOwnerDto> itemOwnerDtoList = RequestMapper.toItemOwnerDtoList(itemList);
        return RequestMapper.toRequestItemDto(request, itemOwnerDtoList);
    }


}
