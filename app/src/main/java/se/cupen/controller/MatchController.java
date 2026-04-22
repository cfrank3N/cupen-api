package se.cupen.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import se.cupen.dto.MatchDTO;
import se.cupen.service.MatchService;

@RestController
public class MatchController {

  private MatchService matchService;

  public MatchController(MatchService matchService) {
    this.matchService = matchService;
  }

  @GetMapping("/games")
  public ResponseEntity<List<MatchDTO>> fetchAllMatches() {
    return ResponseEntity.ok(matchService.findAllMatches());
  }
}
