package nl.knaw.huygens.timbuctoo.rml.jena;

import nl.knaw.huygens.timbuctoo.rml.rdfshim.RdfLiteral;
import nl.knaw.huygens.timbuctoo.rml.rdfshim.RdfResource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class JenaResource implements RdfResource {

  private final Model model;
  private final RDFNode rdfNode;

  private JenaResource(Model model, RDFNode rdfNode) {
    this.model = model;
    this.rdfNode = rdfNode;
  }

  public static RdfResource fromModel(Model data, Resource resource) {
    return new JenaResource(data, resource);
  }

  @Override
  public Set<RdfResource> out(String predicateUri) {
    if (this.rdfNode.isResource()) {
      return getValues(this.model, this.rdfNode.asResource(), predicateUri);
    } else {
      return new HashSet<>();
    }
  }

  @Override
  public Optional<String> asIri() {
    if (this.rdfNode.isResource()) {
      Resource resource = rdfNode.asResource();
      String uri = resource.getURI();
      return uri == null ? Optional.of(resource.getId().getLabelString()) : Optional.of(uri);
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<RdfLiteral> asLiteral() {
    if (this.rdfNode.isLiteral()) {
      return Optional.of(new JenaLiteral(rdfNode.asLiteral()));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public String toString() {
    return rdfNode.toString();
  }

  private Set<RdfResource> getValues(Model model, Resource resource, String uri) {
    Set<RdfResource> result = new HashSet<>();
    try (StatementIterator it = new StatementIterator(model, resource, uri)) {
      while (it.hasNext()) {
        result.add(new JenaResource(model, it.next()));
      }
    }
    return result;
  }

  private class StatementIterator implements Iterator<RDFNode>, AutoCloseable {
    private final StmtIterator stmtIterator;

    private StatementIterator(Model model, Resource resource, String uri) {
      stmtIterator = resource.listProperties(model.createProperty(uri));
    }

    @Override
    public void close() {
      stmtIterator.close();
    }

    @Override
    public boolean hasNext() {
      return stmtIterator.hasNext();
    }

    @Override
    public RDFNode next() {
      return stmtIterator.next().getObject();
    }
  }

}
