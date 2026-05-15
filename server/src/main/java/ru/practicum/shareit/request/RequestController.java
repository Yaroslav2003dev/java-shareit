package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestDto> addRequest(@RequestBody RequestDto requestDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return new ResponseEntity<>(requestService.addRequest(requestDto, userId), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RequestItemDto>> getMyRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return new ResponseEntity<>(requestService.getMyRequests(userId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDto>> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return new ResponseEntity<>(requestService.getAllRequests(userId), HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestItemDto> getByRequestId(@PathVariable Long requestId) {
        return new ResponseEntity<>(requestService.getByRequestId(requestId), HttpStatus.OK);
    }

}
