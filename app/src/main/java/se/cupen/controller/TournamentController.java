package se.cupen.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import se.cupen.dto.TournamentDTO;
import se.cupen.dto.creation.CreateTournament;
import se.cupen.service.TournamentService;
import se.cupen.util.ResponseData;

@RestController("/api")
public class TournamentController {

  private TournamentService service;

  public TournamentController(TournamentService tournamentService) {
    this.service = tournamentService;
  }

  @GetMapping("/tournaments")
  public ResponseEntity<ResponseData<List<TournamentDTO>>> fetchAllTournaments() {
    return ResponseEntity.ok(service.allTournaments());
  }

  @PostMapping("/tournaments")
  public ResponseEntity<ResponseData<TournamentDTO>> createTournament(@RequestBody CreateTournament tournament) {

    ResponseData<TournamentDTO> response = service.createTournament(tournament);

    return ResponseEntity.status(201).body(response);
  }
}
