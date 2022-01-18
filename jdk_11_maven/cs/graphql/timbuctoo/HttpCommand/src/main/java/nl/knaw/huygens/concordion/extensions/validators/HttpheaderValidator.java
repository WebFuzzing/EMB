package nl.knaw.huygens.concordion.extensions.validators;

import nl.knaw.huygens.concordion.extensions.ActualResult;
import nl.knaw.huygens.concordion.extensions.ExpectedResult;
import nl.knaw.huygens.concordion.extensions.ResultValidator;
import nl.knaw.huygens.concordion.extensions.ValidationResult;
import nl.knaw.huygens.contractdiff.diffresults.DiffResult;
import nl.knaw.huygens.contractdiff.httpdiff.ExpectedHeadersAreEqualValidator;

public class HttpheaderValidator implements ResultValidator {
  @Override
  public ValidationResult validate(ExpectedResult expectation, ActualResult reality) {
    DiffResult res = ExpectedHeadersAreEqualValidator.validate(expectation.getHeaders(), reality.getMultiHeaders());

    return ValidationResult.xmlResult(res.wasSuccess(), res.asHtml());
  }
}
