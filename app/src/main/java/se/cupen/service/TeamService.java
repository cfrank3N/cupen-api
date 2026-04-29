package se.cupen.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import se.cupen.dto.TeamDTO;
import se.cupen.dto.creation.CreateTeam;
import se.cupen.exception.ValidationException;
import se.cupen.mapper.TeamMapper;
import se.cupen.persistence.model.Player;
import se.cupen.persistence.model.Team;
import se.cupen.persistence.model.Tournament;
import se.cupen.persistence.repository.PlayerRepo;
import se.cupen.persistence.repository.TeamRepo;
import se.cupen.persistence.repository.TournamentRepo;
import se.cupen.util.ResponseData;

@Service
public class TeamService {

  private final TeamRepo teamRepo;
  private final PlayerRepo playerRepo;
  private final TournamentRepo tournamentRepo;

  public TeamService(TeamRepo teamRepo, PlayerRepo playerRepo, TournamentRepo tournamentRepo) {
    this.teamRepo = teamRepo;
    this.playerRepo = playerRepo;
    this.tournamentRepo = tournamentRepo;
  }

  public ResponseData<List<TeamDTO>> createTeams(List<CreateTeam> teams, String tournamentId) {

    Tournament tournament = findTournamentById(tournamentId);

    validateAllPlayersAreUnique(teams);

    List<List<Player>> playerCouples = validateThatAllPlayersHaveNoTeamInTournament(teams, tournament);

    List<Team> teamsToSave = playerCouples.stream()
        .map(couple -> Team.builder()
            .players(couple)
            .tournament(tournament)
            .build())
        .toList();

    List<TeamDTO> savedTeams = teamRepo.saveAll(teamsToSave).stream().map(TeamMapper::toDTO).toList();

    return ResponseData.successful(savedTeams, "Teams saved");

  }

  /**
   * @param playerIds
   * @return
   */
  private boolean validateAllPlayersAreUnique(List<CreateTeam> teams) {

    List<String> playerIds = teams.stream().flatMap(team -> team.getPlayerIds().stream()).toList();

    if (playerIds.size() != playerIds.stream().distinct().count()) {
      throw new ValidationException("A player can't be in multiple teams", 400);
    } else {
      return true;
    }

  }

  private List<List<Player>> validateThatAllPlayersHaveNoTeamInTournament(List<CreateTeam> teams,
      Tournament tournament) {

    List<List<Player>> playersCouples = teams.stream()
        .map(team -> {
          if (team.getPlayerIds().size() < 2) {
            throw new ValidationException("Team must consist of at least two players", 400);
          }
          return team.getPlayerIds().stream()
              .map(this::findPlayerById).toList();
        })
        .toList();

    playersCouples.forEach(couple -> couple.forEach(player -> {
      boolean alreadyInTournament = player.getTeams().stream()
          .anyMatch(team -> team.getTournament().getId().equals(tournament.getId()));

      if (alreadyInTournament) {
        throw new ValidationException(player.getName() + " is already in a team this tournament", 400);
      }
    }));

    return playersCouples;

  }

  /**
   * @param playerId
   * @return
   */
  private Player findPlayerById(String playerId) {

    return playerRepo.findById(validateIdAndTransformToUuid(playerId))
        .orElseThrow(() -> new ValidationException("Player not found", 404));

  }

  /**
   * @param id
   * @return
   */
  private UUID validateIdAndTransformToUuid(String id) {
    try {
      if (id.length() != 36) {
        throw new ValidationException("Id must be of type UUID v4", 400);
      }
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new ValidationException("Id must be of type UUID v4", 400);
    }
  }

  /**
   * @param tournamentId
   * @return
   */
  private Tournament findTournamentById(String tournamentId) {

    return tournamentRepo.findById(validateIdAndTransformToUuid(tournamentId))
        .orElseThrow(() -> new ValidationException("Tournament not found", 404));

  }

}
