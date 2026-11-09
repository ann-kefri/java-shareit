package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public ResponseEntity<Object> create(Long userId, NewItemDto dto) {
        return post(API_PREFIX, userId, dto);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, UpdateItemDto dto) {
        return patch(API_PREFIX + "/{itemId}", userId, Map.of("itemId", itemId), dto);
    }

    public ResponseEntity<Object> getById(Long userId, Long itemId) {
        return get(API_PREFIX + "/{itemId}", userId, Map.of("itemId", itemId));
    }

    public ResponseEntity<Object> getAllByOwner(Long userId) {
        return get(API_PREFIX, userId);
    }

    public ResponseEntity<Object> search(Long userId, String text) {
        return get(API_PREFIX + "/search?text={text}", userId, Map.of("text", text));
    }

    public ResponseEntity<Object> createComment(Long userId, Long itemId, NewCommentDto dto) {
        return post(API_PREFIX + "/" + itemId + "/comment", userId, dto);
    }
}