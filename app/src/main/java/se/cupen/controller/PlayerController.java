package se.cupen.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.cupen.dto.PlayerDTO;
import se.cupen.dto.creation.CreatePlayer;
import se.cupen.service.PlayerService;
import se.cupen.util.ResponseData;

@RestController
@RequestMapping("/api")
public class PlayerController {

  private PlayerService playerService;

  public PlayerController(PlayerService playerService) {
    this.playerService = playerService;
  }

  @GetMapping("/players")
  public ResponseEntity<List<PlayerDTO>> fetchAllPlayers() {
    return ResponseEntity.ok(playerService.findAllPlayers());
  }

  @PostMapping("/players")
  public ResponseEntity<ResponseData<List<PlayerDTO>>> insertPlayers(@RequestBody List<CreatePlayer> players) {
    ResponseData<List<PlayerDTO>> response = playerService.insertPlayers(players);
    return ResponseEntity.status(response.getStatus()).body(response);
  }
}
