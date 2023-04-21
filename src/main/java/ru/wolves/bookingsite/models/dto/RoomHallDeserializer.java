package ru.wolves.bookingsite.models.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.wolves.bookingsite.models.RoomHall;
import ru.wolves.bookingsite.repositories.RoomHallRepo;

import java.io.IOException;

@Component
public class RoomHallDeserializer extends JsonDeserializer<RoomHall> {
    private final RoomHallRepo roomHallRepo;

    @Autowired
    public RoomHallDeserializer(RoomHallRepo roomHallRepo) {
        this.roomHallRepo = roomHallRepo;
    }

    public RoomHallDeserializer() {
        this.roomHallRepo = null;
    }

    @Override
    public RoomHall deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException{
        Long placeId = jsonParser.getValueAsLong();
        RoomHall roomHall = roomHallRepo.findById(placeId).orElseThrow(() -> new IllegalArgumentException("Invalid placeId: " + placeId));
        return roomHall;
    }
}

