package nl.knaw.huygens.concordion.extensions.validators;

import nl.knaw.huygens.concordion.extensions.ActualResult;
import nl.knaw.huygens.concordion.extensions.ExpectedResult;
import nl.knaw.huygens.concordion.extensions.ResultValidator;
import nl.knaw.huygens.concordion.extensions.ValidationResult;
import nl.knaw.huygens.contractdiff.diffresults.DiffResult;
import nl.knaw.huygens.contractdiff.jsondiff.JsonDiffer;

import java.io.IOException;

public class JsonBodyValidator implements ResultValidator {

  private final JsonDiffer differ;
  private final boolean checkHeader;

  public JsonBodyValidator(boolean checkHeader, JsonDiffer differ) {
    this.differ = differ;
    this.checkHeader = checkHeader;
  }

  @Override
  public ValidationResult validate(ExpectedResult expectation, ActualResult reality) {
    //FIXME check if result contains json content-type

    DiffResult res = null;
    try {
      res = differ.diff(expectation.getBody(), reality.getBody());
    } catch (IOException e) {
      return ValidationResult.result(false, "Could not parse expectation or reality as JSON :\nreality:\n" + reality);
    }

    return ValidationResult.xmlResult(res.wasSuccess(), res.asHtml());
  }
}
