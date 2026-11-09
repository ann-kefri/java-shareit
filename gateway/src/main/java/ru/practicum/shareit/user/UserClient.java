package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import java.util.Map;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public ResponseEntity<Object> create(NewUserDto dto) {
        return post(API_PREFIX, dto);
    }

    public ResponseEntity<Object> update(Long id, UpdateUserDto dto) {
        return patch(API_PREFIX + "/{id}", null, Map.of("id", id), dto);
    }

    public ResponseEntity<Object> getById(Long id) {
        return get(API_PREFIX + "/{id}", null, Map.of("id", id));
    }

    public ResponseEntity<Object> getAll() {
        return get(API_PREFIX);
    }

    public ResponseEntity<Object> delete(Long id) {
        return delete(API_PREFIX + "/{id}", null, Map.of("id", id));
    }
}