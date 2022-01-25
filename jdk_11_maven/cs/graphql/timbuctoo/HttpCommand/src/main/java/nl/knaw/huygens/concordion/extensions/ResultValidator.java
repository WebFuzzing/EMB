package nl.knaw.huygens.concordion.extensions;

public interface ResultValidator {
  ValidationResult validate(ExpectedResult expectation, ActualResult reality);
}
