package nl.knaw.huygens.timbuctoo.v5.rdfio.implementations.rdf4j.parsers;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.RDFParserFactory;
import org.eclipse.rdf4j.rio.helpers.NTriplesParserSettings;
import org.eclipse.rdf4j.rio.nquads.NQuadsParser;

import java.util.Stack;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.eclipse.rdf4j.rio.RDFFormat.NO_NAMESPACES;
import static org.eclipse.rdf4j.rio.RDFFormat.NO_RDF_STAR;
import static org.eclipse.rdf4j.rio.RDFFormat.SUPPORTS_CONTEXTS;

public class NquadsUdParser extends NQuadsParser {
  private static final RDFFormat NQUAD_UD_FORMAT = new RDFFormat(
    "NQuadsUnifiedDiff",
    "application/vnd.timbuctoo-rdf.nquads_unified_diff",
    UTF_8,
    "nqud",
    NO_NAMESPACES,
    SUPPORTS_CONTEXTS,
    NO_RDF_STAR
  );
  private final Stack<Integer> actions;

  public NquadsUdParser() {
    actions = new Stack<>();
  }

  @Override
  protected void parseStatement() throws RDFParseException, RDFHandlerException {
    boolean ignoredAnError = false;
    try {
      skipWhitespace(false);
      if (!shouldParseLine() || !parseDiff()) {
        return;
      }

      skipWhitespace(true);
      if (!shouldParseLineAfterDiff()) {
        return;
      }

      parseSubject();

      skipWhitespace(true);

      parsePredicate();

      skipWhitespace(true);

      parseObject();

      skipWhitespace(true);

      parseContext();

      skipWhitespace(true);

      assertLineTerminates();
    } catch (RDFParseException e) {
      if (getParserConfig().isNonFatalError(NTriplesParserSettings.FAIL_ON_INVALID_LINES)) {
        reportError(e, NTriplesParserSettings.FAIL_ON_INVALID_LINES);
        ignoredAnError = true;
      } else {
        throw e;
      }
    }
    handleStatement(ignoredAnError);
  }

  protected boolean parseDiff() {
    if (this.lineChars[this.currentIndex] == '+' || this.lineChars[this.currentIndex] == '-') {
      int action = this.lineChars[this.currentIndex];

      int curIdx = ++this.currentIndex;
      while (curIdx < this.lineChars.length && (this.lineChars[curIdx] == ' ' || this.lineChars[curIdx] == '\t')) {
        ++curIdx;
      }

      if (this.lineChars[curIdx] == '<' || this.lineChars[curIdx] == '_') {
        actions.push(action);
      }

      return true;
    }

    return false;
  }

  protected boolean shouldParseLineAfterDiff() {
    if (this.currentIndex < this.lineChars.length - 1) {
      if (this.lineChars[this.currentIndex] == '<' || this.lineChars[this.currentIndex] == '_') {
        return true;
      }

      if (this.lineChars[this.currentIndex] == '#' && this.rdfHandler != null) {
        this.rdfHandler.handleComment(
            new String(this.lineChars, this.currentIndex + 1, this.lineChars.length - this.currentIndex - 1));
      }
    }

    return false;
  }

  @Override
  public RDFParser setRDFHandler(RDFHandler handler) {
    if (handler instanceof TimRdfHandler) {
      // It might be nicer to override statement, to make it contain the action, but it takes to much effort for now.
      ((TimRdfHandler) handler).registerActionSupplier(actions::pop);
    }
    return super.setRDFHandler(handler);
  }

  @Override
  public RDFFormat getRDFFormat() {
    return NQUAD_UD_FORMAT;
  }

  public static class NquadsUdParserFactory implements RDFParserFactory {
    @Override
    public RDFFormat getRDFFormat() {
      return NQUAD_UD_FORMAT;
    }

    @Override
    public RDFParser getParser() {
      return new NquadsUdParser();
    }
  }
}
