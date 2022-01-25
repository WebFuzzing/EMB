package nl.knaw.huygens.timbuctoo.server;

import com.google.common.collect.ImmutableMap;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.CollectionBuilder;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.model.vre.vres.VresBuilder;

import static nl.knaw.huygens.timbuctoo.model.properties.PropertyTypes.localProperty;
import static nl.knaw.huygens.timbuctoo.model.properties.PropertyTypes.wwPersonNameOrTempName;
import static nl.knaw.huygens.timbuctoo.model.properties.PropertyTypes.wwdocumentDisplayNameProperty;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.altNames;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.arrayToEncodedArray;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.datable;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.defaultFullPersonNameConverter;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.gender;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.hyperlinks;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.personNames;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.stringArrayToEncodedArrayOf;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.stringOfYesNoUnknown;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.stringToEncodedStringOf;
import static nl.knaw.huygens.timbuctoo.model.properties.converters.Converters.stringToUnencodedStringOf;

@Deprecated
public class HuygensIng {

  private static final String[] DOCUMENT_TYPES = new String[]{
    "UNKNOWN",
    "ANTHOLOGY",
    "ARTICLE",
    "AWARD",
    "CATALOGUE",
    "COMPILATION",
    "DIARY",
    "LETTER",
    "LIST",
    "MONOGRAPH",
    "PERIODICAL",
    "PICTURE",
    "PUBLICITY",
    "SHEETMUSIC",
    "THEATERSCRIPT",
    "WORK"
  };
  @Deprecated
  public static Vres mappings = new VresBuilder()
    .withVre("WomenWriters", "ww", vre -> vre
      .withKeywordTypes(
        ImmutableMap.<String, String>builder()
          .put("hasEducation","education")
          .put("hasFinancialSituation","financialSituation")
          .put("hasGenre","genre")
          .put("hasMaritalStatus","maritalStatus")
          .put("hasProfession","profession")
          .put("hasReligion","religion")
          .put("hasSocialClass","socialClass")
          .build()
      )
      .withCollection("wwcollectives", c -> c
        .withDisplayName(localProperty("wwcollective_name"))

        .withProperty("name", localProperty("wwcollective_name"))
        .withProperty("type", localProperty("wwcollective_type", stringToUnencodedStringOf(
          "UNKNOWN",
          "ACADEMY",
          "ASSOCIATION",
          "LIBRARY",
          "PUBLISHER",
          "SHOP"
        )))
        .withProperty("links", localProperty("wwcollective_links", hyperlinks))

        .withProperty("tempLocationPlacename", localProperty("wwcollective_tempLocationPlacename"))
        .withProperty("tempOrigin", localProperty("wwcollective_tempOrigin"))
        .withProperty("tempShortName", localProperty("wwcollective_tempShortName"))
        .withProperty("tempType", localProperty("wwcollective_tempType"))
        .withProperty("tempOrigin", localProperty("wwcollective_tempOrigin"))
      )
      .withCollection("wwpersons", c -> c
        .withDisplayName(wwPersonNameOrTempName())

        .withProperty("names", localProperty("wwperson_names", personNames))
        .withProperty("types", localProperty("wwperson_types", stringArrayToEncodedArrayOf(
          "ARCHETYPE",
          "AUTHOR",
          "PSEUDONYM",
          "READER"
        )))
        .withProperty("gender", localProperty("wwperson_gender", gender))
        .withProperty("birthDate", localProperty("wwperson_birthDate", datable))
        .withProperty("deathDate", localProperty("wwperson_deathDate", datable))
        .withProperty("bibliography", localProperty("wwperson_bibliography"))
        .withProperty("notes", localProperty("wwperson_notes"))
        .withProperty("children", localProperty("wwperson_children", stringOfYesNoUnknown))
        .withProperty("links", localProperty("wwperson_links", hyperlinks))

        //for passing acceptance test. I don't think these are actually used
        .withProperty("health", localProperty("wwperson_health"))
        .withProperty("livedIn", localProperty("wwperson_livedIn"))
        .withProperty("nationality", localProperty("wwperson_nationality"))
        .withProperty("personalSituation", localProperty("wwperson_personalSituation"))

        .withProperty("tempBirthPlace", localProperty("wwperson_tempBirthPlace"))
        .withProperty("tempChildren", localProperty("wwperson_tempChildren"))
        .withProperty("tempCollaborations", localProperty("wwperson_tempCollaborations"))
        .withProperty("tempDeath", localProperty("wwperson_tempDeath"))
        .withProperty("tempDeathPlace", localProperty("wwperson_tempDeathPlace"))
        .withProperty("tempFinancialSituation", localProperty("wwperson_tempFinancialSituation"))
        .withProperty("tempMemberships", localProperty("wwperson_tempMemberships"))
        .withProperty("tempMotherTongue", localProperty("wwperson_tempMotherTongue"))
        .withProperty("tempName", localProperty("wwperson_tempName"))
        .withProperty("tempOldId", localProperty("wwperson_tempOldId"))
        .withProperty("tempPlaceOfBirth", localProperty("wwperson_tempPlaceOfBirth"))
        .withProperty("tempPsChildren", localProperty("wwperson_tempPsChildren"))
        .withProperty("tempPseudonyms", localProperty("wwperson_tempPseudonyms"))
        .withProperty("tempSpouse", localProperty("wwperson_tempSpouse"))
      )
      .withCollection("wwdocuments", c -> c
        .withDisplayName(wwdocumentDisplayNameProperty())

        .withProperty("title", localProperty("wwdocument_title"))
        .withProperty("englishTitle", localProperty("wwdocument_englishTitle"))
        .withProperty("documentType", localProperty("wwdocument_documentType", stringToEncodedStringOf(
                DOCUMENT_TYPES
        )))
        .withProperty("date", localProperty("wwdocument_date", datable))
        .withProperty("reference", localProperty("wwdocument_reference"))
        .withProperty("notes", localProperty("wwdocument_notes"))
        .withProperty("links", localProperty("wwdocument_links", hyperlinks))
        .withProperty("tempCreator", localProperty("wwdocument_tempCreator"))
        .withProperty("tempLanguage", localProperty("wwdocument_tempLanguage"))
        .withProperty("tempOldId", localProperty("wwdocument_tempOldId"))
        .withProperty("tempOrigin", localProperty("wwdocument_tempOrigin"))
      )
      .withCollection("wwkeywords", c -> c
        .withDisplayName(localProperty("wwkeyword_value"))
        .withProperty("value", localProperty("wwkeyword_value"))
      )
      .withCollection("wwlanguages", c -> c
        .withDisplayName(localProperty("wwlanguage_name"))
        .withProperty("name", localProperty("wwlanguage_name"))
      )
      .withCollection("wwlocations", c -> c
        .withDisplayName(localProperty("wwlocation_name"))
        .withProperty("name", localProperty("wwlocation_name"))
      )
      .withCollection("wwrelations", CollectionBuilder::isRelationCollection)
    )
    .withVre("cwno", "cwno", vre -> vre
      .withCollection("cwnopersons", c -> c
        .withDisplayName(localProperty("cwnoperson_names", defaultFullPersonNameConverter))
        .withProperty("names", localProperty("cwnoperson_names", personNames))
        .withProperty("types", localProperty("cwnoperson_types", stringArrayToEncodedArrayOf(
          "AUTHOR",
          "PSEUDONYM"
        )))
        .withProperty("gender", localProperty("cwnoperson_gender", gender))
        .withProperty("birthDate", localProperty("cwnoperson_birthDate", datable))
        .withProperty("deathDate", localProperty("cwnoperson_deathDate", datable))
        .withProperty("notes", localProperty("cwnoperson_notes"))
        .withProperty("links", localProperty("cwnoperson_links", hyperlinks))
        .withProperty("nationalities", localProperty("cwnoperson_nationalities", arrayToEncodedArray)))
      .withCollection("cwnodocuments", c -> c
        .withDisplayName(localProperty("cwnodocument_title"))
        .withProperty("title", localProperty("cwnodocument_title"))
        .withProperty("englishTitle", localProperty("cwnodocument_englishTitle"))
        .withProperty("documentType", localProperty("cwnodocument_documentType", stringToEncodedStringOf(
                DOCUMENT_TYPES
        )))
        .withProperty("date", localProperty("cwnodocument_date", datable))
        .withProperty("notes", localProperty("cwnodocument_notes"))
        .withProperty("links", localProperty("cwnodocument_links", hyperlinks)))
      .withCollection("cwnorelations", CollectionBuilder::isRelationCollection))
    .withVre("cwrs", "cwrs", vre -> vre
      .withCollection("cwrscollectives", c -> c
        .withDisplayName(localProperty("cwrscollective_name"))

        .withProperty("name", localProperty("cwrscollective_name"))
        .withProperty("type", localProperty("cwrscollective_type", stringToUnencodedStringOf(
          "LIBRARY",
          "PUBLISHER"
        )))
        .withProperty("links", localProperty("cwrscollective_links", hyperlinks))

        .withProperty("tempLocation", localProperty("cwrscollective_tempLocation"))
        .withProperty("tempName", localProperty("cwrscollective_tempName"))
      )
      .withCollection("cwrspersons", c -> c
        .withDisplayName(localProperty("cwrsperson_names", defaultFullPersonNameConverter))

        .withProperty("names", localProperty("cwrsperson_names", personNames))
        .withProperty("types", localProperty("cwrsperson_types", stringArrayToEncodedArrayOf(
          "AUTHOR",
          "PSEUDONYM"
        )))
        .withProperty("gender", localProperty("cwrsperson_gender", gender))
        .withProperty("birthDate", localProperty("cwrsperson_birthDate", datable))
        .withProperty("deathDate", localProperty("cwrsperson_deathDate", datable))
        .withProperty("links", localProperty("cwrsperson_links", hyperlinks))

        .withProperty("tempBirthPlace", localProperty("cwrsperson_tempBirthPlace"))
        .withProperty("tempDeathPlace", localProperty("cwrsperson_tempDeathPlace"))
        .withProperty("tempLanguage", localProperty("cwrsperson_tempLanguage"))
      )
      .withCollection("cwrsdocuments", c -> c
        .withDisplayName(localProperty("cwrsdocument_title"))

        .withProperty("title", localProperty("cwrsdocument_title"))
        .withProperty("documentType", localProperty("cwrsdocument_documentType", stringToEncodedStringOf(
                DOCUMENT_TYPES
        )))
        .withProperty("documentResourceType",
          localProperty("cwrsdocument_documentResourceType", stringToEncodedStringOf(
            "TEXT"
          ))
        )
        .withProperty("date", localProperty("cwrsdocument_date", datable))
        .withProperty("description", localProperty("cwrsdocument_description"))
        .withProperty("links", localProperty("cwrsdocument_links", hyperlinks))
        .withProperty("tempLanguage", localProperty("cwrsdocument_tempLanguage")))
      .withCollection("cwrslanguages", c -> c
        .withDisplayName(localProperty("cwrslanguage_name"))
        .withProperty("name", localProperty("cwrslanguage_name")))
      .withCollection("cwrslocations", c -> c
        .withDisplayName(localProperty("cwrslocation_name"))
        .withProperty("name", localProperty("cwrslocation_name")))
      .withCollection("cwrsrelations", CollectionBuilder::isRelationCollection))
    .withVre("DutchCaribbean", "dcar", vre -> vre
    .withCollection("dcarpersons",
      c -> c
        .withDisplayName(localProperty("dcarperson_label"))
        .withProperty("gender", localProperty("dcarperson_gender", gender))
        .withProperty("names", localProperty("dcarperson_names", personNames))
        .withProperty("links", localProperty("dcarperson_links", hyperlinks)))
    .withCollection("dcarkeywords",
      c -> c
        .withDisplayName(localProperty("dcarkeyword_label"))
        .withProperty("type", localProperty("dcarkeyword_type", stringToUnencodedStringOf(
          "subject",
          "geography")))
        .withProperty("value", localProperty("dcarkeyword_value")))
    .withCollection("dcararchives",
      c -> c
        .withDisplayName(localProperty("dcararchive_titleNld"))
        .withProperty("beginDate", localProperty("dcararchive_beginDate"))
        .withProperty("countries", localProperty("dcararchive_countries", arrayToEncodedArray))
        .withProperty("endDate", localProperty("dcararchive_endDate"))
        .withProperty("extent", localProperty("dcararchive_extent"))
        .withProperty("itemNo", localProperty("dcararchive_itemNo"))
        .withProperty("madeBy", localProperty("dcararchive_madeBy"))
        .withProperty("notes", localProperty("dcararchive_notes"))
        .withProperty("origFilename", localProperty("dcararchive_origFilename"))
        .withProperty("periodDescription", localProperty("dcararchive_periodDescription"))
        .withProperty("refCode", localProperty("dcararchive_refCode"))
        .withProperty("refCodeArchive", localProperty("dcararchive_refCodeArchive"))
        .withProperty("reminders", localProperty("dcararchive_reminders"))
        .withProperty("scope", localProperty("dcararchive_scope"))
        .withProperty("series", localProperty("dcararchive_series"))
        .withProperty("subCode", localProperty("dcararchive_subCode"))
        .withProperty("titleEng", localProperty("dcararchive_titleEng"))
        .withProperty("titleNld", localProperty("dcararchive_titleNld")))
    .withCollection("dcararchivers",
      c -> c
        .withDisplayName(localProperty("dcararchiver_nameNld"))
        .withProperty("beginDate", localProperty("dcararchiver_beginDate"))
        .withProperty("endDate", localProperty("dcararchiver_endDate"))
        .withProperty("history", localProperty("dcararchiver_history"))
        .withProperty("madeBy", localProperty("dcararchiver_madeBy"))
        .withProperty("nameEng", localProperty("dcararchiver_nameEng"))
        .withProperty("nameNld", localProperty("dcararchiver_nameNld"))
        .withProperty("notes", localProperty("dcararchiver_notes"))
        .withProperty("origFilename", localProperty("dcararchiver_origFilename"))
        .withProperty("reminders", localProperty("dcararchiver_reminders"))
        .withProperty("types", localProperty("dcararchiver_types", stringArrayToEncodedArrayOf(
              "corporate_body",
              "person",
              "family"
      ))))
    .withCollection("dcarlegislations",
      c -> c
        .withDisplayName(localProperty("dcarlegislation_titleNld"))
        .withProperty("contents", localProperty("dcarlegislation_contents"))
        .withProperty("date1", localProperty("dcarlegislation_date1"))
        .withProperty("date2", localProperty("dcarlegislation_date2"))
        .withProperty("madeBy", localProperty("dcarlegislation_madeBy"))
        .withProperty("origFilename", localProperty("dcarlegislation_origFilename"))
        .withProperty("originalArchivalSource", localProperty("dcarlegislation_originalArchivalSource"))
        .withProperty("otherPublications", localProperty("dcarlegislation_otherPublications", arrayToEncodedArray))
        .withProperty("pages", localProperty("dcarlegislation_pages"))
        .withProperty("reference", localProperty("dcarlegislation_reference"))
        .withProperty("remarks", localProperty("dcarlegislation_remarks"))
        .withProperty("seeAlso", localProperty("dcarlegislation_seeAlso"))
        .withProperty("titleEng", localProperty("dcarlegislation_titleEng"))
        .withProperty("titleNld", localProperty("dcarlegislation_titleNld")))
    .withCollection("dcarrelations", CollectionBuilder::isRelationCollection))
    .withVre("Admin", "", vre -> vre
    .withCollection("persons", c -> c
      .withDisplayName(localProperty("person_names", defaultFullPersonNameConverter))
      .withProperty("names", localProperty("person_names", personNames))
      .withProperty("types", localProperty("person_types", stringArrayToEncodedArrayOf(
              "ARCHETYPE",
              "AUTHOR",
              "PSEUDONYM",
              "READER",
              ""
      )))
      .withProperty("gender", localProperty("person_gender", gender))
      .withProperty("birthDate", localProperty("person_birthDate", datable))
      .withProperty("deathDate", localProperty("person_deathDate", datable))
      .withProperty("floruit", localProperty("person_floruit"))
      .withProperty("links", localProperty("person_links", hyperlinks)))
    .withCollection("documents", c -> c
      .withDisplayName(localProperty("document_title"))
      .withProperty("title", localProperty("document_title"))
      .withProperty("documentType", localProperty("document_documentType", stringToEncodedStringOf(
              DOCUMENT_TYPES
      )))
      .withProperty("date", localProperty("document_date", datable))
      .withProperty("reference", localProperty("document_reference"))
      .withProperty("links", localProperty("document_links", hyperlinks)))
    .withCollection("collectives", c -> c
      .withDisplayName(localProperty("collective_name"))
      .withProperty("name", localProperty("collective_name"))
      .withProperty("type", localProperty("collective_type", stringToUnencodedStringOf(
        "UNKNOWN",
        "ACADEMY",
        "ASSOCIATION",
        "LIBRARY",
        "PUBLISHER",
        "SHOP"
      )))
      .withProperty("links", localProperty("collective_links", hyperlinks)))
    .withCollection("keywords", c -> c
      .withDisplayName(localProperty("keyword_value"))
      .withProperty("type", localProperty("keyword_type"))
      .withProperty("value", localProperty("keyword_value")))
    .withCollection("languages", c -> c
      .withDisplayName(localProperty("language_name"))
      .withProperty("name", localProperty("language_name")))
    .withCollection("locations", c -> c
      .withDisplayName(localProperty("location_name"))
      .withProperty("name", localProperty("location_name"))
    )
    .withCollection("archives", c -> c
      .withDisplayName(localProperty("tim_id"))
    )
    .withCollection("archivers", c -> c
      .withDisplayName(localProperty("tim_id"))
    )
    .withCollection("legislations", c -> c
      .withDisplayName(localProperty("tim_id"))
    )
    .withCollection("relations", CollectionBuilder::isRelationCollection))
    .withVre("ckcc", "ckcc", vre -> vre
    .withCollection("ckccpersons", c -> c
      .withDisplayName(localProperty("ckccperson_names", defaultFullPersonNameConverter))
      .withProperty("names", localProperty("ckccperson_names", personNames))
      .withProperty("birthDate", localProperty("ckccperson_birthDate", datable))
      .withProperty("deathDate", localProperty("ckccperson_deathDate", datable))
      .withProperty("gender", localProperty("ckccperson_gender", gender))
      .withProperty("cenId", localProperty("ckccperson_cenId"))
      .withProperty("floruit", localProperty("ckccperson_floruit"))
      .withProperty("notes", localProperty("ckccperson_notes"))
      .withProperty("urn", localProperty("ckccperson_urn"))
      .withProperty("links", localProperty("ckccperson_links", hyperlinks)))
    .withCollection("ckcccollectives", c -> c
      .withDisplayName(localProperty("ckcccollective_name"))
      .withProperty("name", localProperty("ckcccollective_name"))
      .withProperty("urn", localProperty("ckcccollective_urn"))
      .withProperty("links", localProperty("ckcccollective_links", hyperlinks))
      .withProperty("type", localProperty("ckcccollective_type", stringToUnencodedStringOf(
        "UNKNOWN"
      ))))
    .withCollection("ckcclocations", c -> c
      .withDisplayName(localProperty("ckcclocation_name"))
      .withProperty("name", localProperty("ckcclocation_name")))
    .withCollection("ckccrelations", CollectionBuilder::isRelationCollection))
    .withVre("Base", "base", v -> v
      .withCollection("baselocations", c -> c.withDisplayName(localProperty("tim_id")))
      .withCollection("baselanguages", c -> c.withDisplayName(localProperty("tim_id"))))
    .withVre("cnw", "cnw", vre -> vre
    .withCollection("cnwpersons", c -> c
      .withDisplayName(localProperty("cnwperson_names", defaultFullPersonNameConverter))
      .withProperty("names", localProperty("cnwperson_names", personNames))
      .withProperty("birthDate", localProperty("cnwperson_birthDate", datable))
      .withProperty("deathDate", localProperty("cnwperson_deathDate", datable))
      .withProperty("gender", localProperty("cnwperson_gender", gender))
      .withProperty("floruit", localProperty("cnwperson_floruit"))
      .withProperty("links", localProperty("cnwperson_links", hyperlinks))
      .withProperty("types", localProperty("cnwperson_types"))
      .withProperty("name", localProperty("cnwperson_name"))
      .withProperty("koppelnaam", localProperty("cnwperson_koppelnaam"))
      .withProperty("networkDomains", localProperty("cnwperson_networkDomains", arrayToEncodedArray))
      .withProperty("domains", localProperty("cnwperson_domains", arrayToEncodedArray))
      .withProperty("subDomains", localProperty("cnwperson_subdomains", arrayToEncodedArray))
      .withProperty("combinedDomains", localProperty("cnwperson_combinedDomains", arrayToEncodedArray))
      .withProperty("characteristics", localProperty("cnwperson_characteristics", arrayToEncodedArray))
      .withProperty("periodicals", localProperty("cnwperson_periodicals", arrayToEncodedArray))
      .withProperty("memberships", localProperty("cnwperson_memberships", arrayToEncodedArray))
      .withProperty("biodesurl", localProperty("cnwperson_biodesurl"))
      .withProperty("dbnlUrl", localProperty("cnwperson_dbnlUrl"))
      .withProperty("verwijzingen", localProperty("cnwperson_verwijzingen", arrayToEncodedArray))
      .withProperty("notities", localProperty("cnwperson_notities"))
      .withProperty("opmerkingen", localProperty("cnwperson_opmerkingen"))
      .withProperty("aantekeningen", localProperty("cnwperson_aantekeningen"))
      .withProperty("altNames", localProperty("cnwperson_altNames", altNames))
      .withProperty("relatives", localProperty("cnwperson_relatives", arrayToEncodedArray))
      .withProperty("cnwBirthYear", localProperty("cnwperson_cnwBirthYear", datable))
      .withProperty("cnwDeathYear", localProperty("cnwperson_cnwDeathYear", datable))
      .withProperty("birthdateQualifier", localProperty("cnwperson_birthdateQualifier"))
      .withProperty("deathdateQualifier", localProperty("cnwperson_deathdateQualifier"))
      .withProperty("shortDescription", localProperty("cnwperson_shortDescription"))
      ))
    .withVre("EuropeseMigratie", "em", vre -> vre
      .withCollection("emcardcatalogs", coll -> coll
        .withAbstractType("collective")
        .withProperty("naam", localProperty("emcardcatalog_name"))
        .withDisplayName(localProperty("emcardcatalog_name", stringToUnencodedStringOf()))
      )
      .withCollection("emconsulates", coll -> coll
        .withAbstractType("collective")
        .withProperty("naam", localProperty("emconsulate_name"))
        .withDisplayName(localProperty("emconsulate_name", stringToUnencodedStringOf()))
      )
      .withCollection("emcards", coll -> coll
        .withAbstractType("document")
        .withProperty("kaartnummer", localProperty("emcard_cardnumber"))
        .withProperty("jaartal_begin", localProperty("emcard_yearStart"))
        .withProperty("jaartal_eind", localProperty("emcard_yearEnd"))
      )
      .withCollection("emmigrantunits", coll -> coll
        .withAbstractType("person")
        .withProperty("persoonsId", localProperty("emmigrantunit_persoonsId"))
        .withProperty("achternaam", localProperty("emmigrantunit_achternaam"))
        .withProperty("tussenvoegsel", localProperty("emmigrantunit_tussenvoegsel"))
        .withProperty("initialen", localProperty("emmigrantunit_initialen"))
        .withProperty("geboortejaar", localProperty("emmigrantunit_geboortejaar"))
        .withProperty("schema", localProperty("emmigrantunit_schema"))
        .withProperty("unit", localProperty("emmigrantunit_unit"))
        .withProperty("samenstelling", localProperty("emmigrantunit_samenstelling"))
        .withProperty("geslacht", localProperty("emmigrantunit_geslacht"))
        .withProperty("godsdienst", localProperty("emmigrantunit_godsdienst"))
        .withProperty("adresAantal", localProperty("emmigrantunit_adresAantal"))
        .withProperty("eventsAantal", localProperty("emmigrantunit_eventsAantal"))
      )
      .withCollection("emlocations", coll -> coll
        .withAbstractType("location")
        .withProperty("naam", localProperty("emlocation_name"))
        .withDisplayName(localProperty("emlocation_name", stringToUnencodedStringOf()))
      )
      .withCollection("emrelations", CollectionBuilder::isRelationCollection)
    )
    .withVre("CharterPortaal", "charter", vre -> vre
        .withCollection("chartercollectives", c -> c
            .withDisplayName(localProperty("chartercollective_name"))
            .withProperty("name", localProperty("chartercollective_name"))
            .withProperty("type", localProperty("chartercollective_type", stringToUnencodedStringOf(
              "ARCHIVE"
            )))
            .withProperty("links", localProperty("chartercollective_links", hyperlinks))
          )
          .withCollection("charterdocuments", c -> c
            .withDisplayName(localProperty("charterdocument_title"))
            .withProperty("title", localProperty("charterdocument_title"))
            .withProperty("archief", localProperty("charterdocument_archief"))
            .withProperty("fonds", localProperty("charterdocument_fonds"))
            .withProperty("fondsNaam", localProperty("charterdocument_fondsNaam"))
            .withProperty("inventarisNummer", localProperty("charterdocument_inventarisNummer"))
            .withProperty("inventaristekst", localProperty("charterdocument_inventaristekst", arrayToEncodedArray))
            .withProperty("volgNummer", localProperty("charterdocument_volgnummer"))
            .withProperty("regestNummer", localProperty("charterdocument_regestnummer"))
            .withProperty("thumbs", localProperty("charterdocument_thumbs", hyperlinks))
            .withProperty("links", localProperty("charterdocument_links", hyperlinks))
            .withProperty("documentType", localProperty("charterdocument_documentType", stringToEncodedStringOf(
                    DOCUMENT_TYPES
            )))
            .withProperty("date", localProperty("charterdocument_date", datable))
            .withProperty("tekstRegest", localProperty("charterdocument_tekstRegest", arrayToEncodedArray))
            .withProperty("additioneleInformatie", localProperty("charterdocument_additioneleInformatie"))
            .withProperty("transcription", localProperty("charterdocument_transcription"))
            .withProperty("translation", localProperty("charterdocument_translation"))
            .withProperty("descriptionOfEditions", localProperty("charterdocument_descriptionOfEditions"))
            .withProperty("namesInCharter", localProperty("charterdocument_namesInCharter"))
            .withProperty("overige", localProperty("charterdocument_overige"))
          )
          .withCollection("charterrelations", CollectionBuilder::isRelationCollection)
      )
    .build();

}
