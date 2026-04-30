package se.cupen.controller;

import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;

import se.cupen.dto.MatchDTO;
import se.cupen.dto.MatchEventDTO;
import se.cupen.dto.creation.CreateMatch;
import se.cupen.dto.creation.CreateMatchEvent;
import se.cupen.service.MatchService;
import se.cupen.util.ResponseData;

@RestController
@RequestMapping("/api")
public class MatchController {

  private MatchService matchService;

  public MatchController(MatchService matchService) {
    this.matchService = matchService;
  }

  @GetMapping("/matches")
  public ResponseEntity<List<MatchDTO>> fetchAllMatches() {
    return ResponseEntity.ok(matchService.findAllMatches());
  }

  @PostMapping("/matches")
  public ResponseEntity<ResponseData<List<MatchDTO>>> createMatches(@RequestBody List<CreateMatch> matches) {
    ResponseData<List<MatchDTO>> response = matchService.createMatches(matches);
    return ResponseEntity.status(HttpStatus.SC_CREATED).body(response);
  }

  @PostMapping("/matches/events")
  public ResponseEntity<ResponseData<List<MatchEventDTO>>> createMatchEvents(
      @RequestBody List<CreateMatchEvent> events) {
    ResponseData<List<MatchEventDTO>> response = matchService.createMatchEvents(events);
    return ResponseEntity.status(HttpStatus.SC_CREATED).body(response);
  }
}
