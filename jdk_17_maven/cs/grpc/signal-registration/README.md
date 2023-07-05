# Signal registration service

This is a multi-provider phone number verification service for use with Signal.

When Signal users first create an account, they do so by associating that account with a phone number. Signal verifies that users actually control that phone number by sending a verification code to that number via SMS or via a phone call. This service manages the process of sending verification codes and checking codes provided by clients.

## Major components

External callers interact with this service by sending [gRPC](https://grpc.io/) requests. The gRPC interface is defined in [`registration_service.proto`](./src/main/proto/registration_service.proto). gRPC requests are handled by [`RegistrationServiceGrpcEndpoint`](./src/main/java/org/signal/registration/rpc/RegistrationServiceGrpcEndpoint.java), which sanitizes client input and dispatches requests to [`RegistrationService`](./src/main/java/org/signal/registration/RegistrationService.java), which orchestrates the major business logic for the entire service.

`RegistrationService` uses a [`SenderSelectionStrategy`](./src/main/java/org/signal/registration/sender/SenderSelectionStrategy.java) to choose a concrete [`VerificationCodeSender`](./src/main/java/org/signal/registration/sender/VerificationCodeSender.java) implementation to send a verification code to a client. `VerificationCodeSenders` are responsible for sending verification codes via a specific transport (i.e. SMS or voice) and service provider and later for verifying codes provided by clients. A [`SessionRepository`](./src/main/java/org/signal/registration/session/SessionRepository.java) stores session data (i.e. verification codes or references to external verification sessions) for `VerificationCodeSenders`.

## Configuration

At a minimum, the registration service needs at least one `VerificationCodeSender`, a `SenderSelectionStrategy`, and a `SessionRepository`. No beans of those types will be instantiated unless they're configured, and so some configuration properties must be provided. The following table describes the currently-supported (and required, in production environments) configuration properties.

| Property                                                               | Description                                                                                                                                                                                                                                                              |
|------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `analytics.bigtable.table-id`                                          | The identifier for a Cloud Bigtable table to be used to store verification attempts pending follow-up analysis (optional)                                                                                                                                                |
| `analytics.bigtable.column-family-name`                                | The name of a column family within `analytics.bigtable.table-id` to be used to store verification attempts pending follow-up analysis (optional)                                                                                                                         |
| `analytics.pubsub.topic`                                               | The name of a GCP pub/sub topic to which to send events when attempts are fully analyzed                                                                                                                                                                                 |
| `fictitious-numbers.firestore.collection-name`                         | The name of the Cloud Firestore collection that stores verification codes for fictitious phone numbers                                                                                                                                                                   |
| `fictitious-numbers.firestore.expiration-field-name`                   | The name of the field in documents in the Cloud Firestore collection for verification codes for fictitious phone numbers that identifies when the document expires and may be removed automatically                                                                      |
| `gcp.bigtable.project-id`                                              | The identifier for a Google Cloud Platform project that contains the Cloud Bigtable instance to be used for various data storage applications                                                                                                                            |
| `gcp.bigtable.instance-id`                                             | The identifier for a Cloud Bigtable instance to be used for various data storage applications                                                                                                                                                                            |
| `messagebird.access-key`                                               | The access key used to authenticate with MessageBird                                                                                                                                                                                                                     |
| `messagebird.default-sender-id`                                        | The default originating number or alphanumeric sender id for MessageBird messages and calls                                                                                                                                                                              |
| `messagebird.region-sender-ids`                                        | The originating number or alphanumeric sender id for MessageBird messages and calls by region                                                                                                                                                                            |
| `messagebird.sms.session-ttl`                                          | The maximum lifetime of a registration started by sending a verification code via MessageBird sms (optional)                                                                                                                                                             |
| `messagebird.verify.session-ttl`                                       | The maximum lifetime of a registration started by sending a verification code via MessageBird verify (optional)                                                                                                                                                          |
| `messagebird.voice.session-ttl`                                        | The maximum lifetime of a registration started by sending a verification code via MessageBird voice (optional)                                                                                                                                                           |
| `messagebird.voice.supported-languages`                                | A list of BCP 47 language tags for which translations of spoken messages delivered via the MessageBird Voice API are available                                                                                                                                           |
| `prescribed-verification-codes.firestore.collection-name`              | The name of the Cloud Firestore collection that contains prescribed verification codes                                                                                                                                                                                   |
| `rate-limits.check-verification-code.delays`                           | A list of durations that callers must wait between successive attempts to check verification codes                                                                                                                                                                       |
| `rate-limits.leaky-bucket.session-creation.max-capacity`               | The maximum number of permits in a session creation rate limiter "bucket"                                                                                                                                                                                                |
| `rate-limits.leaky-bucket.session-creation.permit-regeneration-period` | The time required for a permit in a session creation rate limiter "bucket" to regenerate                                                                                                                                                                                 |
| `rate-limits.leaky-bucket.session-creation.min-delay`                  | The minimum amount of time that must elapse between taking permits from a session creation rate limiter "bucket"                                                                                                                                                         |
| `rate-limits.send-sms-verification-code.delays`                        | A list of durations that callers must wait between successive attempts to send verification codes via SMS                                                                                                                                                                |
| `rate-limits.send-voice-verification-code.delay-after-first-sms`       | The amount of time a caller must wait after requesting their first SMS before they may request a phone call                                                                                                                                                              |
| `rate-limits.send-voice-verification-code.delays`                      | A list of durations that callers must wait between successive attempts to send verification codes via phone calls                                                                                                                                                        |
| `selection.[transport].fallback-senders`                               | A nonempty ordered list of senders to fall back on. The first sender that supports the request will be used, or the first sender if no sender supports the request.                                                                                                      |
| `selection.[transport].default-weights`                                | A map by service of weights by which requests are assigned to services. (e.g. twilio-verify = 50, messagebird-verify = 50) (optional)                                                                                                                                    |
| `selection.[transport].region-weights`                                 | A map by region of weights that override the default weights (e.g. us.twilio-verify = 9, us.messagebird-verify = 1) (optional)                                                                                                                                           |
| `selection.[transport].region-overrides`                               | A map of regions to service that indicate that a region should always go to that service (e.g. de = messagebird-verify) (optional)                                                                                                                                       |
| `session-repository.bigtable.table-name`                               | The name of the Bigtable table that contains registration sessions                                                                                                                                                                                                       |
| `session-repository.bigtable.column-family-name`                       | The name of the Bigtable column family name within the configured Bigtable table that contains registration sessions                                                                                                                                                     |
| `twilio.account-sid`                                                   | The SID of the Twilio account to use to send verification codes via Twilio's [Programmable Messaging](https://www.twilio.com/messaging/programmable-messaging-api), [Programmable Voice](https://www.twilio.com/voice), and [Verify](https://www.twilio.com/verify) APIs |
| `twilio.api-key-sid`                                                   | The SID of the Twilio API key used to authenticate with Twilio                                                                                                                                                                                                           |
| `twilio.api-key-secret`                                                | The secret component of the API key used to authenticate with Twilio                                                                                                                                                                                                     |
| `twilio.messaging.nanpa-messaging-service-sid`                         | The SID of the Twilio messaging service to be used to send SMS messages to [NANPA](https://nationalnanpa.com/) phone numbers                                                                                                                                             |
| `twilio.messaging.global-messaging-service-sid`                        | The SID of the Twilio messaging service to be used to send SMS messages to phone numbers outside of NANPA                                                                                                                                                                |
| `twilio.messaging.session-ttl`                                         | The maximum lifetime of a registration started by sending a verification code via the Twilio Programmable Messaging API (optional)                                                                                                                                       |
| `twilio.voice.phone-numbers`                                           | A list of [E.164](https://www.twilio.com/docs/glossary/what-e164)-formatted phone numbers from which Twilio voice calls can originate                                                                                                                                    |
| `twilio.voice.cdn-uri`                                                 | The base URI from which voice messages translated to various languages may be retrieved                                                                                                                                                                                  |
| `twilio.voice.supported-languages`                                     | A list of BCP 47 language tags for which translations of spoken messages delivered via the Twilio Programmable Voice API are available                                                                                                                                   |
| `twilio.voice.session-ttl`                                             | The maximum lifetime of a registration started by sending a verification code via the Twilio Programmable Voice API (optional)                                                                                                                                           |
| `twilio.verify.service-sid`                                            | The SID of a Twilio Verify service to be used to send verification codes                                                                                                                                                                                                 |
| `twilio.verify.service-friendly-name`                                  | A "friendly" name for the Twilio Verify service, which may appear in verification messages (optional)                                                                                                                                                                    |
| `twilio.verify.android-app-hash`                                       | The app hash to include in SMS messages sent by Twilio Verify for Android devices that support [Automatic SMS Verification](https://developers.google.com/identity/sms-retriever/overview)                                                                               |
| `twilio.verify.supported-languages`                                    | A list of BCP 47 language tags supported by Twilio Verify                                                                                                                                                                                                                |
| `verification.sms.android-app-hash`                                    | The app hash to include in SMS messages for Android devices that support [Automatic SMS Verification](https://developers.google.com/identity/sms-retriever/overview)                                                                                                     |
| `verification.sms.message-variants-by-region`                          | A map of two-letter region codes (e.g. "US") to names of SMS message variants; message variants should have corresponding entries in the SMS message string table                                                                                                        |
| `verification.[transport].supported-languages`                         | A list of [BCP 47](https://www.rfc-editor.org/rfc/rfc4646.txt) language tags for which translations of a verification SMS message sent via the Twilio Programmable Messaging API are available                                                                           |

### Running in development mode

For local testing, this service can be run in the `dev` [Micronaut environment](https://docs.micronaut.io/latest/guide/#environments). In the `dev` environment, the following components are provided (assuming no others of have been configured):

- A trivial verification code sender that always uses the last six digits of a phone number as a verification code
- A trivial sender selection strategy that always chooses the last-six-digits "sender"
- An in-memory session store

These components are, obviously, not suitable for production use and are intended only to facilitate local development and testing.

To run the registration service locally with the `dev` environment enabled:

```shell
./mvnw mn:run -Dmicronaut.environments=dev
```

## Testing with command-line tools

The registration service include a set of CLI tools to facilitate testing and development. The tools allow operators to create and inspect registration sessions, send verification codes, and check verification codes.

To build the CLI tools:

```shell
./mvnw clean package
```

To run the tool and get an exhaustive list of subcommands and flags:

```shell
java -cp target/registration-service-0.1.jar org.signal.registration.cli.RegistrationClient
```

…which yields (at the time of writing):

```
Usage: <main class> [--api-key=<apiKey>] [--host=<host>] [--port=<port>]
                    [[--plaintext] |
                    --trusted-server-certificate=<trustedServerCertificate>]
                    [COMMAND]
      --api-key=<apiKey>   API key for this call
      --host=<host>        Registration service hostname (default: localhost)
      --plaintext          Use plaintext instead of TLS? (default: false)
      --port=<port>        Registration service port (default: 50051)
      --trusted-server-certificate=<trustedServerCertificate>
                           Path to a trusted server certificate; signal.org
                           certificate trusted by default
Commands:
  create-session, create          Start a new registration session
  get-session, get                Describe an existing registration session
  send-verification-code, send    Send a verification code to a phone number
                                  associated with a session
  check-verification-code, check  Check a verification code for a registration
                                  session
```

As a concrete example of interacting with a local registration service running on port 50051, callers may create a registration session with the following command:

```shell
java -cp target/registration-service-0.1.jar org.signal.registration.cli.RegistrationClient \
  --host=localhost \
  --port=50051 \
  --plaintext \
  create-session +18005550123
```

…which yields (in this example):

```
Created registration session 2a3d2a2a41ff41fb9ce41687ddcc51a4
```

To send an SMS verification code for that session:

```shell
java -cp target/registration-service-0.1.jar org.signal.registration.cli.RegistrationClient \
  --host=localhost \
  --port=50051 \
  --plaintext \
  send-verification-code 2a3d2a2a41ff41fb9ce41687ddcc51a4
```

…and, finally, to submit a verification code for that session:

```shell
java -cp target/registration-service-0.1.jar org.signal.registration.cli.RegistrationClient \
  --host=localhost \
  --port=50051 \
  --plaintext \
  check-verification-code 2a3d2a2a41ff41fb9ce41687ddcc51a4 550123
```
