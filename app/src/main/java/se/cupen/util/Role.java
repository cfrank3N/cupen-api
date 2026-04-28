package se.cupen.util;

public enum Role {
  ADMIN(1);

  private final int rank;

  private Role(int rank) {
    this.rank = rank;
  }

  public boolean outranks(Role role) {
    return this.rank >= role.rank;
  }
}
