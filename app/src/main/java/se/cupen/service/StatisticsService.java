package se.cupen.service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import se.cupen.dto.PlayerSpecificMatchDTO;
import se.cupen.exception.ValidationException;
import se.cupen.mapper.MatchMapper;
import se.cupen.persistence.model.Match;
import se.cupen.persistence.model.Player;
import se.cupen.persistence.model.Team;
import se.cupen.persistence.repository.MatchEventRepo;
import se.cupen.persistence.repository.MatchRepo;
import se.cupen.persistence.repository.PlayerRepo;
import se.cupen.util.ResponseData;

@Service
public class StatisticsService {

  private final MatchRepo matchRepo;
  private final MatchEventRepo matchEventRepo;
  private final PlayerRepo playerRepo;

  public StatisticsService(MatchRepo matchRepo, MatchEventRepo matchEventRepo, PlayerRepo playerRepo) {
    this.matchEventRepo = matchEventRepo;
    this.matchRepo = matchRepo;
    this.playerRepo = playerRepo;
  }

  // TODO: Add non required RequestParams like year, matchType, etc.
  /**
   * @param playerId
   * @return
   */
  public ResponseData<List<PlayerSpecificMatchDTO>> findAllMatchesPlayedByPlayer(String playerId) {

    Player player = playerRepo.findById(validateIdAndTransformToUuid(playerId))
        .orElseThrow(() -> new ValidationException("Player not found", 404));

    List<Team> playersTeams = player.getTeams();

    List<PlayerSpecificMatchDTO> playersMatches = matchRepo.findAll().stream()
        .filter(match -> playersTeams.contains(match.getTeamA())
            || playersTeams.contains(match.getTeamB()))
        .sorted(Comparator.comparing(Match::getPlayedAt).reversed())
        .map(match -> MatchMapper.toPlayerSpecificDTO(match, playersTeams)).toList();

    return ResponseData.successful(playersMatches, "Matches fetched");

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
}
