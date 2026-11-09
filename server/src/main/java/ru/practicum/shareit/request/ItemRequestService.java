package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, NewItemRequestDto dto);

    List<ItemRequestDto> getAllByRequestor(Long userId);

    List<ItemRequestDto> getAllForOtherUsers(Long userId);

    ItemRequestDto getById(Long userId, Long requestId);
}
