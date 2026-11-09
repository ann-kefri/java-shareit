package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateUser() {
        NewUserDto dto = new NewUserDto();
        dto.setName("Тестовый пользователь");
        dto.setEmail("testuser@integration.ru");

        UserDto created = userService.create(dto);

        assertNotNull(created.getId());
        assertEquals("Тестовый пользователь", created.getName());
        assertEquals("testuser@integration.ru", created.getEmail());
    }
}