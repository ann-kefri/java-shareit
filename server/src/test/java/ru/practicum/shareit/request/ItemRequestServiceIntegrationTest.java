package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService requestService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateRequest() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@request.ru");
        user = userRepository.save(user);

        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Нужна вещь");

        ItemRequestDto created = requestService.create(user.getId(), dto);

        assertNotNull(created.getId());
        assertEquals("Нужна вещь", created.getDescription());
        assertEquals(user.getId(), created.getRequestor().getId());
    }
}