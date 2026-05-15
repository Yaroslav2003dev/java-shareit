package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(RequestDto requestDto, Long userId);

    List<RequestItemDto> getMyRequests(Long userId);

    List<RequestDto> getAllRequests(Long userId);

    RequestItemDto getByRequestId(Long requestId);
}
