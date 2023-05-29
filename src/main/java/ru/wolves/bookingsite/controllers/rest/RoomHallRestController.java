package ru.wolves.bookingsite.controllers.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.wolves.bookingsite.exceptions.PlaceIsNotFoundException;
import ru.wolves.bookingsite.models.RoomHall;
import ru.wolves.bookingsite.services.impl.RoomHallServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/roomHall")
public class RoomHallRestController {

    public final RoomHallServiceImpl roomHallService;

    public RoomHallRestController(RoomHallServiceImpl roomHallService) {
        this.roomHallService = roomHallService;
    }


    @GetMapping
    public ResponseEntity<?> findAll(){
        List<RoomHall> roomHalls = roomHallService.findAllRoomHall();
        return ResponseEntity.ok().body(roomHalls);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findOne(@PathVariable Long id) throws PlaceIsNotFoundException {
        RoomHall roomHall = roomHallService.findRoom(id);
        return ResponseEntity.ok().body(roomHall);
    }
    @PostMapping
    public ResponseEntity<?> createNew(@RequestBody RoomHall roomHall){
        roomHallService.saveRoom(roomHall);
        return ResponseEntity.ok().body("Room was created successfully");
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateRoomHall(@PathVariable Long id, @RequestBody RoomHall roomHall) throws PlaceIsNotFoundException {
        roomHallService.updateRoom(id, roomHall);
        return ResponseEntity.ok().body("Room was updated successfully");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoomHall(@PathVariable Long id) throws PlaceIsNotFoundException {
        roomHallService.deleteRoomHall(id);
        return ResponseEntity.ok().body("Room was deleted successfully");
    }
    @ExceptionHandler
    private ResponseEntity<?> handle(PlaceIsNotFoundException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
