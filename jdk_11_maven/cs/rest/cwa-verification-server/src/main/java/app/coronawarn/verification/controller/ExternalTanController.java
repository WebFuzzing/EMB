package app.coronawarn.verification.controller;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import app.coronawarn.verification.config.VerificationApplicationConfig;
import app.coronawarn.verification.domain.VerificationAppSession;
import app.coronawarn.verification.exception.VerificationServerException;
import app.coronawarn.verification.model.AppSessionSourceOfTrust;
import app.coronawarn.verification.model.HashedGuid;
import app.coronawarn.verification.model.LabTestResult;
import app.coronawarn.verification.model.RegistrationToken;
import app.coronawarn.verification.model.Tan;
import app.coronawarn.verification.model.TanSourceOfTrust;
import app.coronawarn.verification.model.TestResult;
import app.coronawarn.verification.service.AppSessionService;
import app.coronawarn.verification.service.FakeDelayService;
import app.coronawarn.verification.service.FakeRequestService;
import app.coronawarn.verification.service.TanService;
import app.coronawarn.verification.service.TestResultServerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * This class represents the rest controller for external tan interactions.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/version/v1")
@Validated
@Profile("external")
public class ExternalTanController {

  /**
   * The route to the tan generation endpoint.
   */
  public static final String TAN_ROUTE = "/tan";
  private static final Integer RESPONSE_PADDING_LENGTH = 15;
  private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(4);

  @NonNull
  private final AppSessionService appSessionService;

  @NonNull
  private final FakeDelayService fakeDelayService;

  @NonNull
  private final VerificationApplicationConfig verificationApplicationConfig;

  @NonNull
  private final TestResultServerService testResultServerService;

  @NonNull
  private final TanService tanService;

  @NonNull
  private final FakeRequestService fakeRequestService;

  /**
   * This method generates a transaction number by a Registration Token, if the state of the COVID-19 lab-test is
   * positive.
   *
   * @param registrationToken generated by a hashed guid or a teleTAN. {@link RegistrationToken}
   * @param fake              flag for fake request
   * @return A generated transaction number {@link Tan}.
   */
  @Operation(
    summary = "Generates a Tan",
    description = "Generates a TAN on input of Registration Token. With the TAN one can submit his Diagnosis keys"
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Registration Token is valid"),
    @ApiResponse(responseCode = "400", description = "Registration Token does not exist")})
  @PostMapping(value = TAN_ROUTE,
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public DeferredResult<ResponseEntity<Tan>> generateTan(@Valid @RequestBody RegistrationToken registrationToken,
                                                         @RequestHeader(value = "cwa-fake", required = false)
                                                           String fake) {
    if ((fake != null) && (fake.equals("1"))) {
      return fakeRequestService.generateTan(registrationToken);
    }
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Optional<VerificationAppSession> actual
      = appSessionService.getAppSessionByToken(registrationToken.getRegistrationToken());
    if (actual.isPresent()) {
      VerificationAppSession appSession = actual.get();
      int tancountermax = verificationApplicationConfig.getAppsession().getTancountermax();
      if (appSession.getTanCounter() < tancountermax) {
        AppSessionSourceOfTrust appSessionSourceOfTrust = appSession.getSourceOfTrust();
        TanSourceOfTrust tanSourceOfTrust = TanSourceOfTrust.CONNECTED_LAB;
        switch (appSessionSourceOfTrust) {
          case HASHED_GUID:
            TestResult covidTestResult = testResultServerService.result(new HashedGuid(appSession.getHashedGuid()));
            if (covidTestResult.getTestResult() != LabTestResult.POSITIVE.getTestResult()
              && covidTestResult.getTestResult() != LabTestResult.QUICK_POSITIVE.getTestResult()
            ) {
              stopWatch.stop();
              throw new VerificationServerException(HttpStatus.BAD_REQUEST,
                "Tan cannot be created, caused by the non positive result of the labserver");
            }
            break;
          case TELETAN:
            tanSourceOfTrust = TanSourceOfTrust.TELETAN;
            break;
          default:
            stopWatch.stop();
            throw new VerificationServerException(HttpStatus.BAD_REQUEST,
              "Unknown source of trust inside the appsession for the registration token");
        }
        appSession.incrementTanCounter();
        appSession.setUpdatedAt(LocalDateTime.now());

        appSessionService.saveAppSession(appSession);
        String generatedTan = tanService.generateVerificationTan(tanSourceOfTrust, appSession.getTeleTanType());

        Tan returnTan = generateReturnTan(generatedTan, fake);
        stopWatch.stop();
        fakeDelayService.updateFakeTanRequestDelay(stopWatch.getTotalTimeMillis());
        DeferredResult<ResponseEntity<Tan>> deferredResult = new DeferredResult<>();
        scheduledExecutor.schedule(() -> deferredResult.setResult(
            ResponseEntity.status(HttpStatus.CREATED).body(returnTan)),
          fakeDelayService.realDelayTan(), MILLISECONDS);
        log.info("Returning the successfully generated tan.");
        return deferredResult;
      }
      throw new VerificationServerException(HttpStatus.BAD_REQUEST,
        "The maximum of generating tans for this registration token is reached");
    }
    throw new VerificationServerException(HttpStatus.BAD_REQUEST,
      "VerificationAppSession not found for the registration token");
  }

  private Tan generateReturnTan(String tan, String fake) {
    if (fake == null) {
      return new Tan(tan);
    }
    return new Tan(tan, RandomStringUtils.randomAlphanumeric(RESPONSE_PADDING_LENGTH));
  }

}
