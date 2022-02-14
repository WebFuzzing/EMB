package nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search;

public class FullTextSearchParameter {
  private String name;
  private String term;

  public FullTextSearchParameter(String name, String term) {
    this.name = name;
    this.term = term;
  }

  public FullTextSearchParameter(){

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTerm() {
    return term;
  }

  public void setTerm(String term) {
    this.term = term;
  }
}
