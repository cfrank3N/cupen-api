package se.cupen.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import se.cupen.dto.TournamentDTO;
import se.cupen.dto.creation.CreateTournament;
import se.cupen.exception.ValidationException;
import se.cupen.mapper.TeamMapper;
import se.cupen.persistence.model.Tournament;
import se.cupen.persistence.repository.TournamentRepo;
import se.cupen.util.ResponseData;

@Service
public class TournamentService {

  private final TournamentRepo tournamentRepo;

  public TournamentService(TournamentRepo tournamentRepo) {
    this.tournamentRepo = tournamentRepo;
  }

  public ResponseData<TournamentDTO> createTournament(CreateTournament tournament) {
    tournamentAlredyExists(tournament.getYear());

    Tournament savedTournament = tournamentRepo.save(Tournament.builder()
        .year(tournament.getYear())
        .build());

    TournamentDTO tournamentDTO = TournamentDTO.builder()
        .id(savedTournament.getId())
        .year(savedTournament.getYear())
        .build();

    return ResponseData.successful(tournamentDTO, "Tournament saved");

  }

  private boolean tournamentAlredyExists(int year) {
    List<Tournament> existingTournaments = tournamentRepo.findAll();

    boolean tournamentAlreadyExists = existingTournaments.stream()
        .anyMatch(t -> t.getYear() == year);

    if (tournamentAlreadyExists) {
      throw new ValidationException("Tournament already exists", 400);
    }

    return false;
  }

  public ResponseData<List<TournamentDTO>> allTournaments() {
    List<TournamentDTO> tournaments = tournamentRepo.findAll().stream()
        .sorted(Comparator.comparing(Tournament::getYear))
        .map(t -> {
          return TournamentDTO.builder()
              .id(t.getId())
              .year(t.getYear())
              .teams(t.getTeams().stream().map(TeamMapper::toDTO).toList())
              .build();
        })
        .toList();

    return ResponseData.successful(tournaments, "Tournaments fetched");
  }
}
