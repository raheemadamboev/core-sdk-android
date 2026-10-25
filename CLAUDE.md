# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A single-module Android library, `:CoreSDKAndroid` (namespace `xyz.teamgravity.coresdkandroid`): the extensions, utilities and managers that every Gravity app uses. It has no resources and an empty manifest. JitPack publishes it as `com.github.raheemadamboev:core-sdk-android`. The sibling libraries in `~/Gravity/projects/library/` (`core-sdk-compose`, `core-sdk-view`, `core-sdk-network`, `pin-lock-compose`) depend on it, each pinned to its own version through the `gravity-core` entry in its catalog.

## Commands

```bash
./gradlew :CoreSDKAndroid:assembleRelease      # build the AAR
./gradlew :CoreSDKAndroid:lint                 # lint the debug variant; report in CoreSDKAndroid/build/reports/lint-results-debug.html
./gradlew :CoreSDKAndroid:publishToMavenLocal  # install the current version into ~/.m2, for a consumer that lists mavenLocal()

# What JitPack runs for a tag. Use it to reproduce a failed JitPack build.
./gradlew clean -Pgroup=com.github.raheemadamboev -Pversion=<tag> -xtest -xlint assemble publishToMavenLocal
```

There are no tests: no `src/test` or `src/androidTest`, no test dependencies and no JUnit platform setup.

The Gradle daemon runs on Corretto 21 (`gradle/gradle-daemon-jvm.properties`, provisioned through foojay). JitPack builds with OpenJDK 21 (`jitpack.yml`).

## Build setup

- AGP 9 with built-in Kotlin, so there is no `org.jetbrains.kotlin.android` plugin. Kotlin compiler options go in the `tasks.withType<KotlinCompile>` block of `CoreSDKAndroid/build.gradle.kts`. That block opts the whole module in to `ExperimentalEncodingApi` and `ExperimentalCoroutinesApi`, so the code needs no `@OptIn` for them.
- SDK levels come from `sdk-min`, `sdk-compile` and `sdk-target` in `gradle/libs.versions.toml`. A library has no target SDK of its own, so `sdk-target` only feeds `lint {}`.
- `consumerProguardFiles` points at `proguard-rules.pro`, so the rules in that file ship to every consuming app. `consumer-rules.pro` is empty and unused. The library itself is not minified.
- Every dependency is `implementation`, so consumers compile against `kotlin-stdlib` only. A consumer that uses a type from a dependency (`Flow`, `ActivityResultLauncher`, `TestDispatcher`, `JsonElement`) declares that dependency itself.
- The test helpers in `coroutine/dispatcher` ship in `src/main`. That is why `kotlinx-coroutines-test` is `implementation` and the JUnit Jupiter API is `compileOnly`: consumers bring their own JUnit 5.
- `kotlin-reflect` is there for `Any?.toJsonElement()`, which looks up serializers through `KClass.createType()`.

## Architecture

Each package under `xyz.teamgravity.coresdkandroid` is one feature area. The API comes in two kinds:

- **Stateless helpers:** top-level extensions (`android/*Extension.kt`, `coroutine`, `settings`, `time`, `collection`, `serialization`) and `object` utils (`BuildUtil`, `ColorUtil`, `SizeUtil`, `ConnectUtil`, `TimeUtil`).
- **Managers:** an interface plus a `<Name>Imp` with a plain constructor. The library uses no DI framework; apps construct the `Imp` in a Hilt module and bind the interface. Version 1.0.49 converted `CryptoManager`, `Preferences`, `ReviewManager` and `UpdateManager`. `PermissionChecker`/`AndroidPermissionChecker` and `DispatcherProvider`/`AndroidDispatcherProvider` follow the same split without the `Imp` name. `NotificationManager` is still a plain class. The library ships no fakes; apps write their own.

How the managers fit together:

- `CryptoManagerImp(dispatcher)` ← `PreferencesImp(crypto, dispatcher, context)` ← `ReviewManagerImp(preferences, dispatcher, context)`, which keeps its state under the internal `ReviewPreferences` keys. `UpdateManagerImp(dispatcher, context)` stands alone. Every `Imp` takes a `DispatcherProvider` and switches threads only through it.
- `ReviewManagerImp` and `UpdateManagerImp` own a `CoroutineScope(dispatcher.io + SupervisorJob())`. Their API functions launch fire-and-forget work and report back through `event`, a `Channel` exposed with `receiveAsFlow()`, so each event reaches one collector.
- `result` (`Result<D, E : Error>`, `GeneralError`, `MessageError`) is the error model of every Gravity app and library. `core-sdk-network` maps HTTP status codes and network exceptions onto `GeneralError`.
- `coroutine/dispatcher` is also test support for consumers. `MainDispatcherExtension` (JUnit 5) replaces `Dispatchers.Main`, and the test providers take its scheduler (`UnconfinedTestDispatcherProvider(main.dispatcher.scheduler)`), so the class under test, `Dispatchers.Main` and `runTest` share one virtual clock.

## Compatibility

Production apps depend on this library.

- **Stored data.** Changing any of these silently loses or resets user data in every app:
  - the DataStore file name `CoreSDKAndroid_Preferences` in `PreferencesImp`;
  - the `ReviewPreferences` key strings;
  - in `CryptoManagerImp`, the Keystore alias `secret`, `AES/CBC/PKCS7Padding`, and the Base64 payload layout: IV size as a 4-byte Int, the IV, then the ciphertext. A value that fails to decrypt reads back as the key's default.
  - Encrypted keys store values of every type as String preferences under the same key name, so an existing key cannot change `encrypted` without a migration.
- **`GeneralError`.** Add new values only at the end.
- **Binary compatibility.** Gradle gives an app a single version of this library, the highest any dependency asks for, so the sibling libraries' published binaries run against newer versions than they were compiled with. A change that still compiles for them can still break them at runtime. For example, turning a class into an interface (1.0.49) makes their existing calls fail with `IncompatibleClassChangeError` until they are rebuilt and released. Before changing a public type, check who calls it: `grep -rn "import xyz.teamgravity.coresdkandroid" ~/Gravity/projects/library --include='*.kt'`.
- **Docs that track this API.** `~/.claude-shared/skills/gravity-android-project/references/core-sdk.md` (packages, signatures, Hilt wiring) and `gravity-android-testing` (dispatchers, interfaces, fakes) describe this API with "from <version>" notes. When the API changes, offer the matching edit there.

## Releases

- Each version branch bumps `version` in the `publishing` block of `CoreSDKAndroid/build.gradle.kts`. It must equal the branch and tag name.
- Raheem creates the GitHub release with a tag equal to the version, on the PR's merge commit in `main`. JitPack builds from that tag; its log is at `https://jitpack.io/com/github/raheemadamboev/core-sdk-android/<version>/build.log`.
- `jitpack.yml` runs `./scripts/prepareJitpackEnvironment.sh` before the build. That script has never existed. JitPack logs "No such file or directory" and continues, so it is not why a build failed.

## OpenSpec

OpenSpec is set up (`openspec/config.yaml` with the `spec-driven` schema; `/opsx:*` commands and `openspec-*` skills in `.claude/`), but `openspec/specs/` and `openspec/changes/` are still empty.
