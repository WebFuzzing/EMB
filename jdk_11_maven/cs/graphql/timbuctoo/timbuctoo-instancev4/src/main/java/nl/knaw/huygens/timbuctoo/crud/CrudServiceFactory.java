package nl.knaw.huygens.timbuctoo.crud;

import nl.knaw.huygens.timbuctoo.core.TimbuctooActions;
import nl.knaw.huygens.timbuctoo.experimental.womenwriters.WomenWritersJsonCrudService;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;

public class CrudServiceFactory {

  private final Vres vres;
  private final UserValidator userValidator;
  private final UrlGenerator relationUrlFor;

  public CrudServiceFactory(Vres vres, UserValidator userValidator, UrlGenerator relationUrlFor) {
    this.vres = vres;
    this.userValidator = userValidator;
    this.relationUrlFor = relationUrlFor;
  }

  public JsonCrudService newJsonCrudService(TimbuctooActions timbuctooActions) {
    return new JsonCrudService(vres, userValidator, relationUrlFor, timbuctooActions);
  }

  public WomenWritersJsonCrudService newWomenWritersJsonCrudService(TimbuctooActions timbuctooActions) {
    return new WomenWritersJsonCrudService(vres, userValidator, relationUrlFor, timbuctooActions);
  }


}
