package se.cupen.persistence.seeder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import se.cupen.persistence.model.Match;
import se.cupen.persistence.model.MatchEvent;
import se.cupen.persistence.model.Player;
import se.cupen.persistence.model.Team;
import se.cupen.persistence.model.Tournament;
import se.cupen.persistence.repository.MatchEventRepo;
import se.cupen.persistence.repository.MatchRepo;
import se.cupen.persistence.repository.PlayerRepo;
import se.cupen.persistence.repository.TeamRepo;
import se.cupen.persistence.repository.TournamentRepo;
import se.cupen.util.EventType;
import se.cupen.util.MatchGroup;
import se.cupen.util.MatchType;

@Component
@Profile("dev")
public class DatabaseSeeder {

  private final TournamentRepo tournamentRepo;
  private final MatchRepo matchRepo;
  private final PlayerRepo playerRepo;
  private final TeamRepo teamRepo;
  private final MatchEventRepo matchEventRepo;

  public DatabaseSeeder(
      TournamentRepo tournamentRepo,
      MatchRepo matchRepo,
      MatchEventRepo matchEventRepo,
      PlayerRepo playerRepo,
      TeamRepo teamRepo) {

    this.matchEventRepo = matchEventRepo;
    this.matchRepo = matchRepo;
    this.playerRepo = playerRepo;
    this.teamRepo = teamRepo;
    this.tournamentRepo = tournamentRepo;

  }

  @PostConstruct
  public void seed() {

    Player playerA = Player.builder()
        .city("Nyköping")
        .name("Albin Glysing")
        .pricemoney(500)
        .build();

    Player playerB = Player.builder()
        .city("Stockholm")
        .name("Adam Frank")
        .pricemoney(0)
        .build();

    Player playerC = Player.builder()
        .city("Nyköping")
        .name("Alexander Wålinder")
        .pricemoney(0)
        .build();

    Player playerD = Player.builder()
        .city("Nyköping")
        .name("Emil Rigo")
        .pricemoney(2000)
        .build();

    List<Player> playersA = new ArrayList<>();
    List<Player> playersB = new ArrayList<>();

    playersA.add(playerA);
    playersA.add(playerB);
    playersB.add(playerC);
    playersB.add(playerD);

    Tournament tournament = Tournament.builder()
        .year(2026)
        .build();

    tournamentRepo.save(tournament);

    Team teamA = Team.builder()
        .players(playersA)
        .tournament(tournament)
        .build();

    Team teamB = Team.builder()
        .players(playersB)
        .tournament(tournament)
        .build();

    Match matchOne = Match.builder()
        .playedAt(Instant.now())
        .teamA(teamA)
        .teamB(teamB)
        .tournament(tournament)
        .matchGroup(MatchGroup.GROUP_B)
        .matchType(MatchType.GROUP_STAGE)
        .build();

    MatchEvent eventOne = MatchEvent.builder()
        .match(matchOne)
        .player(playerB)
        .team(teamA)
        .type(EventType.GOAL)
        .build();

    MatchEvent eventTwo = MatchEvent.builder()
        .match(matchOne)
        .player(playerD)
        .team(teamB)
        .type(EventType.GOAL)
        .build();

    MatchEvent eventThree = MatchEvent.builder()
        .match(matchOne)
        .player(playerA)
        .team(teamA)
        .type(EventType.GOAL)
        .build();

    List<MatchEvent> events = List.of(eventOne, eventTwo, eventThree);

    matchOne.getEvents().addAll(events);

    playerRepo.saveAll(List.of(playerA, playerB, playerC, playerD));
    teamRepo.saveAll(List.of(teamA, teamB));
    matchRepo.save(matchOne);
    matchEventRepo.saveAll(events);
  }
}
