# Contributing

How SeniorLight is built and what a change has to respect. How the code fits
together is in [ARCHITECTURE.md](ARCHITECTURE.md).

## Ground rules

- **Simplicity is the product.** One tap on, one tap off. No screen, no
  brightness, no strobe, no SOS, no timer, no settings. A pull request that adds
  a feature will most likely be declined; one that removes complexity is welcome.
- **No dependencies.** Plain Java against the Android platform APIs. No
  AndroidX, Kotlin, Compose or third-party libraries. `testImplementation` of
  JUnit is the only exception.
- **Use the platform.** The light goes through `CameraManager.setTorchMode`, the
  same switch as the system flashlight tile, and the widget always shows the
  state the system reports.
- **English and German.** Every user-visible string goes in both
  `res/values/strings.xml` and `res/values-de/strings.xml`.
- **Big and plain.** High contrast, state shown by colour and by icon, no
  gestures.

## Building

Needs JDK 17 and the Android SDK (API 35). Point Gradle at the SDK with a
gitignored `local.properties` containing `sdk.dir=/path/to/Android/Sdk`.

    ./gradlew assembleDebug     # app/build/outputs/apk/debug/SeniorLight-<version>-debug.apk
    ./gradlew assembleRelease   # signed only if signing is configured, see below

## Testing

Unit tests carry the weight; device testing is kept minimal.

    ./gradlew test        # JVM unit tests (CameraPick)
    ./gradlew lint
    scripts/smoke.sh      # on a device or emulator: the app icon switches the light on and off

Logic that can be tested without a device belongs in a pure Java class (no
`android.*` imports) with a JVM test, like `CameraPick`.

## Release signing

Run once:

    scripts/setup-signing.sh

It creates `~/keys/seniorlight-release.jks`, stores a random password in the
system keyring (`secret-tool lookup service seniorlight key release-keystore`)
and writes the gitignored `keystore.properties`. Back up the keystore and the
password off the machine: without them no future version can update installed
copies. Without any of this the release build is unsigned; debug builds and
tests are unaffected.

## Releasing

1. Bump `appVersionName` and `appVersionCode` in `app/build.gradle.kts`.
   The version code must always go up, or phones refuse the update.
2. `./gradlew test lint assembleRelease`
3. Check that the output is `app/build/outputs/apk/release/SeniorLight-<version>-release.apk`.
   An `-unsigned` name means signing was not configured.
4. Verify the signature with `apksigner verify --print-certs <apk>` against the
   certificate SHA-256 in the latest release notes.
5. Tag `v<version>` and attach the APK to a GitHub release, renamed to
   `SeniorLight-<version>.apk`.
