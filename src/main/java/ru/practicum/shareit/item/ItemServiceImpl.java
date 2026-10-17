package ru.practicum.shareit.item;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto create(Long userId, NewItemDto dto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Item item = ItemMapper.toItem(dto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemDto dto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вещь с id=" + itemId + " не принадлежит пользователю id=" + userId);
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemWithBookingsDto getById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        ItemWithBookingsDto dto = toItemWithBookings(item);

        // Комментарии — видны всем
        dto.setComments(commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .toList());

        // lastBooking/nextBooking — только владельцу
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> bookings = bookingRepository
                    .findAllByItemIdAndStatusOrderByStartAsc(itemId, BookingStatus.APPROVED);

            bookings.stream()
                    .filter(b -> b.getEnd().isBefore(now))
                    .reduce((first, second) -> second)     // последнее из прошедших
                    .ifPresent(b -> dto.setLastBooking(toBookingShort(b)));

            bookings.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .findFirst()                            // ближайшее будущее
                    .ifPresent(b -> dto.setNextBooking(toBookingShort(b)));
        }

        return dto;
    }

    @Override
    public List<ItemWithBookingsDto> getAllByOwner(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        List<Item> items = itemRepository.findAllByOwnerId(userId);

        List<Long> itemIds = items.stream().map(Item::getId).toList();

        Map<Long, List<Booking>> bookingsByItem = itemIds.isEmpty()
                ? Map.of()
                : bookingRepository.findAllByItemIdInAndStatusOrderByStartAsc(itemIds, BookingStatus.APPROVED)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        Map<Long, List<Comment>> commentsByItem = itemIds.isEmpty()
                ? Map.of()
                : commentRepository.findAllByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();

        return items.stream()
                .map(item -> {
                    ItemWithBookingsDto dto = toItemWithBookings(item);

                    List<Booking> bookings = bookingsByItem.getOrDefault(item.getId(), List.of());

                    bookings.stream()
                            .filter(b -> b.getEnd().isBefore(now))
                            .reduce((first, second) -> second)
                            .ifPresent(b -> dto.setLastBooking(toBookingShort(b)));

                    bookings.stream()
                            .filter(b -> b.getStart().isAfter(now))
                            .findFirst()
                            .ifPresent(b -> dto.setNextBooking(toBookingShort(b)));

                    dto.setComments(commentsByItem.getOrDefault(item.getId(), List.of()).stream()
                            .map(CommentMapper::toCommentDto)
                            .toList());

                    return dto;
                })
                .toList();
    }

    private BookingShortDto toBookingShort(Booking booking) {
        BookingShortDto dto = new BookingShortDto();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        return dto;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentDto createComment(Long userId, Long itemId, NewCommentDto dto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        // Проверка: пользователь действительно брал вещь в аренду?
        LocalDateTime now = LocalDateTime.now();
        boolean hasApprovedPastBooking = bookingRepository
                .findAllByItemIdAndStatusOrderByStartAsc(itemId, BookingStatus.APPROVED)
                .stream()
                .anyMatch(b -> b.getBooker().getId().equals(userId) && b.getEnd().isBefore(now));

        if (!hasApprovedPastBooking) {
            throw new ValidationException(
                    "Пользователь с id=" + userId + " не брал вещь с id=" + itemId + " в аренду");
        }

        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(now);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private ItemWithBookingsDto toItemWithBookings(Item item) {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        return dto;
    }
}