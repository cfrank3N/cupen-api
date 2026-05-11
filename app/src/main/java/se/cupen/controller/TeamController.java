package se.cupen.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;

import se.cupen.dto.TeamDTO;
import se.cupen.dto.creation.CreateTeam;
import se.cupen.service.TeamService;
import se.cupen.util.ResponseData;

@RestController
@RequestMapping("/api")
public class TeamController {

  private TeamService teamService;

  public TeamController(TeamService teamService) {
    this.teamService = teamService;
  }

  @GetMapping("/teams")
  public ResponseEntity<ResponseData<List<TeamDTO>>> fetchAllTeams() {
    return ResponseEntity.ok(teamService.findAllTeams());
  }

  @PostMapping("/teams/{tournamentId}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public ResponseEntity<ResponseData<List<TeamDTO>>> createTeams(@RequestBody List<CreateTeam> teams,
      @PathVariable String tournamentId) {

    ResponseData<List<TeamDTO>> response = teamService.createTeams(teams, tournamentId);

    return ResponseEntity.status(HttpStatus.SC_CREATED).body(response);
  }
}
