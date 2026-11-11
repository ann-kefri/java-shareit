package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

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

    @Test
    void testUpdateItem() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@update.ru");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Old");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        UpdateItemDto updateDto = new UpdateItemDto();
        updateDto.setName("New");

        ItemDto updated = itemService.update(owner.getId(), item.getId(), updateDto);

        assertEquals("New", updated.getName());
    }

    @Test
    void testGetById() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@get.ru");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        ItemWithBookingsDto found = itemService.getById(owner.getId(), item.getId());

        assertEquals("Item", found.getName());
    }

    @Test
    void testSearch() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@search.ru");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Дрель");
        item.setDescription("Мощная");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        List<ItemDto> result = itemService.search("дрел");

        assertEquals(1, result.size());
        assertEquals("Дрель", result.get(0).getName());
    }
}