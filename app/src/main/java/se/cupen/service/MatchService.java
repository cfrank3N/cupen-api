package se.cupen.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import se.cupen.dto.MatchDTO;
import se.cupen.mapper.MatchMapper;
import se.cupen.persistence.model.Match;
import se.cupen.persistence.repository.MatchEventRepo;
import se.cupen.persistence.repository.MatchRepo;

@Service
public class MatchService {

  private final MatchRepo matchRepo;
  private final MatchEventRepo matchEventRepo;

  public MatchService(MatchRepo matchRepo, MatchEventRepo matchEventRepo) {
    this.matchEventRepo = matchEventRepo;
    this.matchRepo = matchRepo;
  }

  public List<MatchDTO> findAllMatches() {
    return matchRepo.findAll().stream()
        .sorted(Comparator.comparing(Match::getPlayedAt).reversed())
        .map(match -> MatchMapper.toDTO(match)).toList();
  }
}
