package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, NewItemDto dto);

    ItemDto update(Long userId, Long itemId, UpdateItemDto dto);

    ItemDto getById(Long itemId);

    List<ItemDto> getAllByOwner(Long userId);

    List<ItemDto> search(String text);
}