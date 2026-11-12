package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void testCreateBooking() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@booking.ru");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@booking.ru");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Вещь");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto booking = bookingService.create(booker.getId(), dto);

        assertNotNull(booking.getId());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }
}