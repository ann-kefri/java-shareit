package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateItem() {
        User owner = new User();
        owner.setName("Тестовый владелец");
        owner.setEmail("owner@test.ru");
        owner = userRepository.save(owner);

        NewItemDto dto = new NewItemDto();
        dto.setName("Тестовая дрель");
        dto.setDescription("Для тестов");
        dto.setAvailable(true);

        ItemDto createdItem = itemService.create(owner.getId(), dto);

        assertNotNull(createdItem.getId());
        assertEquals("Тестовая дрель", createdItem.getName());
        assertEquals(true, createdItem.getAvailable());
    }
}