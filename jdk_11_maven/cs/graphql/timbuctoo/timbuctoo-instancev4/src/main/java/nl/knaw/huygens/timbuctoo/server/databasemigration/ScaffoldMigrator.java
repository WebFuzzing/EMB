package nl.knaw.huygens.timbuctoo.server.databasemigration;

import nl.knaw.huygens.timbuctoo.core.dto.dataset.CollectionBuilder;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.TinkerPopOperations;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.model.vre.vres.VresBuilder;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.core.dto.RelationType.relationType;
import static nl.knaw.huygens.timbuctoo.model.properties.PropertyTypes.localProperty;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.datable;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.defaultFullPersonNameConverter;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.personNames;
import static nl.knaw.huygens.timbuctoo.server.databasemigration.TinkerPopOperationsForMigrations.forInitDb;

public class ScaffoldMigrator {
  private static final Logger LOG = LoggerFactory.getLogger(ScaffoldMigrator.class);
  private final TinkerPopGraphManager graphManager;

  public ScaffoldMigrator(TinkerPopGraphManager graphManager) {
    this.graphManager = graphManager;
  }

  //FIXME move to DataAccess (allow ScaffoldVresConfig.mappings to be injected as an argument)
  public void execute() {
    //The migration are executed first, so those vertices _will_ be present, even on a new empty database
    //The code below will add vertices, so a second launch will not run this code
    try (TinkerPopOperations db = forInitDb(graphManager)) {

      if (db.databaseIsEmptyExceptForMigrations()) {
        LOG.info("Setting up a new scaffold for empty database");
        Vres mappings = new VresBuilder()
          .withVre("Admin", "", vre ->
            vre
              .withCollection("persons", collection ->
                collection
                  .withDescription("People with at least a name, birthdate and birthplace.")
                  .withDisplayName(localProperty("person_names", defaultFullPersonNameConverter))
                  .withProperty("names", localProperty("person_names", personNames))
                  .withProperty("gender", localProperty("person_gender"))
                  .withProperty("birthDate", localProperty("person_birthDate", datable))
                  .withProperty("deathDate", localProperty("person_deathDate", datable))
              )
              .withCollection("locations", collection ->
                collection
                  .withDescription("Countries, cities, villages, streets, etc.")
                  .withDisplayName(localProperty("location_name"))
                  .withProperty("name", localProperty("location_name"))
                  .withProperty("country", localProperty("location_country"))
              )
              .withCollection("collectives", collection ->
                collection
                  .withDescription("Institutes, multiple persons, companies, etc.")
                  .withDisplayName(localProperty("collective_name"))
                  .withProperty("name", localProperty("collective_name"))
              )
              .withCollection("documents", collection ->
                collection
                  .withDescription("Stories, novels, letters, diaries, plays, films, etc.")
                  .withDisplayName(localProperty("document_title"))
                  .withProperty("title", localProperty("document_title"))
                  .withProperty("documentType", localProperty("document_documentType"))
                  .withProperty("date", localProperty("document_date", datable))

              )
              .withCollection("keywords", collection -> collection
              .withDescription("")
              .withDisplayName(localProperty("keyword_value"))
                .withProperty("value", localProperty("keyword_value"))
                .withProperty("type", localProperty("keyword_type"))
              )
              .withCollection("concepts", collection ->
                collection
                  .withDescription("Concepts which do not conform to a specific archetype.")
                  .withDisplayName(localProperty("label"))
              )
              .withCollection("relations", CollectionBuilder::isRelationCollection))
          .build();
        db.initDb(
          mappings,
          relationType("person", "hasBirthPlace", "location", "isBirthPlaceOf", false, false, false, UUID.randomUUID()),
          relationType("person", "hasDeathPlace", "location", "isDeathPlaceOf", false, false, false, UUID.randomUUID()),
          relationType("collective", "hasMember", "person", "isMemberOf", false, false, false, UUID.randomUUID()),
          relationType("collective", "locatedAt", "location", "isHomeOf", false, false, false, UUID.randomUUID()),
          relationType("document", "isCreatedBy", "person", "isCreatorOf", false, false, false, UUID.randomUUID()),

          // TODO+FIXME, these BIA relationTypes should be made VRE specific and editable before mapping
          // person to person relations
          relationType("concept", "hasFirstPerson", "person", "isFirstPersonInRelation",
            false, false, false, UUID.randomUUID()
          ),
          relationType("concept", "hasSecondPerson", "person", "isSecondPersonInRelation",
            false, false, false, UUID.randomUUID()
          ),
          relationType("concept", "hasPersonToPersonRelationType", "concept", "isPersonToPersonRelationTypeOf",
            false, false, false, UUID.randomUUID()
          ),

          // states of persons
          relationType("concept", "hasStateType", "concept", "isStateTypeOf", false, false, false, UUID.randomUUID()),
          relationType("concept", "isStateOfPerson", "person", "hasPersonState",
            false, false, false, UUID.randomUUID()
          ),
          relationType("concept", "isStateLinkedToInstitute", "collective", "isInstituteLinkedToState",
            false, false, false, UUID.randomUUID()
          ),
          relationType("concept", "isStateLinkedToLocation", "location", "isLocationLinkedToState",
            false, false, false, UUID.randomUUID()
          ),

          // data lines for persons
          relationType("concept", "hasDataLineType", "concept", "isDataLineTypeOf",
            false, false, false, UUID.randomUUID()
          ),
          relationType("concept", "isDataLineForPerson", "person", "hasDataLine",
            false, false, false, UUID.randomUUID()
          ),

          // scientist_bios for persons
          relationType("concept", "hasFieldOfInterest", "concept", "isFieldOfInterestOf",
            false, false, false, UUID.randomUUID()
          ),
          relationType("concept", "isScientistBioOf", "person", "hasScientistBio",
            false, false, false, UUID.randomUUID()
          )
        );
      }
      db.success();
    }
  }
}
