DROP TABLE ACTIVITY_DERIVED IF EXISTS;

-- Changes to this view should be mirrored in data-access\...\import.sql and scout-api\api\...\migrations.xml -->
CREATE VIEW ACTIVITY_DERIVED AS
  SELECT
    A.ID                            ACTIVITY_ID,
    SUM(CAST(AUR.FAVOURITE AS INT)) FAVOURITES_COUNT,
    SUM(AUR.RATING)                 RATINGS_SUM,
    COUNT(AUR.RATING)               RATINGS_COUNT,
    AVG(AUR.RATING)                 RATINGS_AVG
  FROM ACTIVITY A LEFT JOIN ACTIVITY_RATING AUR ON A.ID = AUR.ACTIVITY_ID
  GROUP BY A.ID;

DROP TABLE tag_derived IF EXISTS;

-- Changes to this view should be mirrored in data-access\...\import.sql and scout-api\api\...\migrations.xml -->
CREATE VIEW tag_derived AS
  SELECT
    T.ID                              TAG_ID,
    COUNT(APT.ACTIVITY_PROPERTIES_ID) ACTIVITIES_COUNT
  FROM TAG T LEFT JOIN ACTIVITY_PROPERTIES_TAG APT ON T.ID = APT.TAG_ID
    INNER JOIN ACTIVITY_PROPERTIES AP ON (APT.ACTIVITY_PROPERTIES_ID = AP.ID AND AP.PUBLISHING_ACTIVITY_ID IS NOT NULL)
  GROUP BY T.ID;

