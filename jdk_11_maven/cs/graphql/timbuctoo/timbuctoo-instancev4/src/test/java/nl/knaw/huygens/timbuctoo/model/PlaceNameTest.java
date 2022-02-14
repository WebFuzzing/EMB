package nl.knaw.huygens.timbuctoo.model;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class PlaceNameTest {

  private PlaceName instance;

  @Before
  public void setUp() {
    instance = new PlaceName();
  }

  @Test
  public void getDefaultNameReturnsNullIfNoLocationTypeIsAvailable() {
    // instance has no location types when no setters are used

    String value = instance.getDefaultName();

    assertThat(value, is(nullValue()));

  }

  @Test
  public void getDefaultNameReturnsTheBlocNameIfItIsTheOneFilled() {
    String blocName = "Europe";
    instance.setBloc(blocName);

    String defaultName = instance.getDefaultName();

    assertThat(defaultName, is(blocName));
  }

  @Test
  public void getDefaultNameReturnsTheCountryNameIfItIsFilledEvenIfTheBlocNameIsFilled() {
    PlaceName placeName = new PlaceName();
    placeName.setBloc("Europe");
    String countryName = "Netherlands";
    placeName.setCountry(countryName);

    String defaultName = placeName.getDefaultName();

    assertThat(defaultName, is(countryName));
  }

  @Test
  public void getDefaultNameReturnsTheRegionNameWithTheCountryIfTheyAreFilledIn() {
    instance.setBloc("Europe");
    instance.setCountry("Netherlands");
    instance.setRegion("Zuid-Holland");

    String defaultName = instance.getDefaultName();

    assertThat(defaultName, is("Zuid-Holland (region, Netherlands)"));
  }

  @Test
  public void getDefaultNameReturnsTheRegionNameWithTheCountryCodeIfTheyAreFilledIn() {
    instance.setBloc("Europe");
    instance.setCountry("Netherlands");
    instance.setCountryCode("NLD");
    instance.setRegion("Zuid-Holland");

    String defaultName = instance.getDefaultName();

    assertThat(defaultName, is("Zuid-Holland (region, NLD)"));
  }

  @Test
  public void getDefaultNameReturnsTheSettlementAndCountryIfTheyAreFilledIn() {
    instance.setBloc("Europe");
    instance.setCountry("Netherlands");
    instance.setRegion("Zuid-Holland");
    instance.setSettlement("Hillegom");

    String defaultName = instance.getDefaultName();

    assertThat(defaultName, is("Hillegom (Zuid-Holland, Netherlands)"));
  }

  @Test
  public void getDefaultNameReturnsTheSettlementAndCountryCodeIfTheyAreFilledIn() {
    instance.setBloc("Europe");
    instance.setCountry("Netherlands");
    instance.setCountryCode("NLD");
    instance.setRegion("Zuid-Holland");
    instance.setSettlement("Hillegom");

    String defaultName = instance.getDefaultName();

    assertThat(defaultName, is("Hillegom (Zuid-Holland, NLD)"));
  }

  @Test
  public void getDefaultNameReturnsTheDistrictTheSettlementTheRegionAndCountryIfTheyAreFilledIn() {
    instance.setBloc("Europe");
    instance.setCountry("Netherlands");
    instance.setRegion("Zuid-Holland");
    instance.setSettlement("Hillegom");
    instance.setDistrict("Elsbroek");

    String defaultName = instance.getDefaultName();

    assertThat(defaultName, is("Elsbroek, Hillegom (Zuid-Holland, Netherlands)"));
  }

  @Test
  public void getDefaultNameReturnsTheDistrictTheSettlementTheRegionAndCountryCodeIfTheyAreFilledIn() {
    instance.setBloc("Europe");
    instance.setCountry("Netherlands");
    instance.setCountryCode("NLD");
    instance.setRegion("Zuid-Holland");
    instance.setSettlement("Hillegom");
    instance.setDistrict("Elsbroek");

    String defaultName = instance.getDefaultName();

    assertThat(defaultName, is("Elsbroek, Hillegom (Zuid-Holland, NLD)"));
  }
}
