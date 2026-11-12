package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public ResponseEntity<Object> create(Long userId, NewItemRequestDto dto) {
        return post(API_PREFIX, userId, dto);
    }

    public ResponseEntity<Object> getAllByRequestor(Long userId) {
        return get(API_PREFIX, userId);
    }

    public ResponseEntity<Object> getAllForOtherUsers(Long userId) {
        return get(API_PREFIX + "/all", userId);
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return get(API_PREFIX + "/{requestId}", userId, java.util.Map.of("requestId", requestId));
    }
}