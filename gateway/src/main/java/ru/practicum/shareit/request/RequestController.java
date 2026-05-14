package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;


@Slf4j
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestBody RequestDto requestDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.addRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getMyRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getMyRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getByRequestId(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        return requestClient.getByRequestId(userId, requestId);
    }
}
