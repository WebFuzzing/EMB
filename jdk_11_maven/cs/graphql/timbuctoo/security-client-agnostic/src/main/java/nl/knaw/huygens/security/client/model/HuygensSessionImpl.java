package nl.knaw.huygens.security.client.model;

import nl.knaw.huygens.security.core.model.HuygensPrincipal;
import nl.knaw.huygens.security.core.model.HuygensSession;

import java.util.UUID;

public class HuygensSessionImpl implements HuygensSession {
  private UUID id;
  private HuygensPrincipal owner;

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public HuygensPrincipal getOwner() {
    return owner;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setOwner(HuygensPrincipal owner) {
    this.owner = owner;
  }
}
