package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, NewItemRequestDto dto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<ItemRequestDto> getAllByRequestor(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(this::enrichWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllForOtherUsers(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
        return requests.stream()
                .map(this::enrichWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + userId + " не найден"));

        return enrichWithItems(request);
    }

    private ItemRequestDto enrichWithItems(ItemRequest request) {
        ItemRequestDto dto = ItemRequestMapper.toDto(request);
        dto.setItems(itemRepository.findAllByRequestId(request.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
        return dto;
    }
}
