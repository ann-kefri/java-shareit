package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public ResponseEntity<Object> create(Long userId, NewBookingDto dto) {
        return post(API_PREFIX, userId, dto);
    }

    public ResponseEntity<Object> approve(Long userId, Long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved, "bookingId", bookingId);
        return patch(API_PREFIX + "/{bookingId}?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        return get(API_PREFIX + "/{bookingId}", userId, Map.of("bookingId", bookingId));
    }

    public ResponseEntity<Object> getAllByBooker(Long userId, String state) {
        return get(API_PREFIX + "?state={state}", userId, Map.of("state", state));
    }

    public ResponseEntity<Object> getAllByOwner(Long userId, String state) {
        return get(API_PREFIX + "/owner?state={state}", userId, Map.of("state", state));
    }
}