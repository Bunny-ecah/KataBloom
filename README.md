# KataBloom 2.2

A native Android word garden for primary learners, with a Bahasa Melayu / English interface switch. Learning words and speech practice stay in Bahasa Melayu.

## Open the correct project

Open this folder in Android Studio, let Gradle sync, select your phone/tablet, then press Run. The app opens to a bilingual welcome page with Sign in, Sign up and Continue without an account. Signed-in parents return to the dashboard. The app name is KataBloom and the version is 2.2.

- Android 6.0+ (API 23), compile/target SDK 36.
- Use Android Studio's bundled JDK (17+, tested with 21).
- Guest/local learning needs no Firebase sign-in.
- Existing Firebase configuration is retained for optional parent accounts and manual cloud backup.

## What's included

- Welcome, sign-in and sign-up screens, with a visible account/logout section in Settings.
- A dashboard with daily mission, effort-star and streak tiles, and a badge trail.
- A seven-day activity chart and independent-answer ring based on real saved results; empty results display a dash.

- 30 bundled Malay words across three levels, with alternating sets of five words.
- Listen and look, identify initial letters, order syllable tiles, and try speaking.
- Gentle hints/retries, no timer or lost lives, and five effort stars per completed round.
- A garden illustration that grows with practice and badges at 1, 3 and 10 rounds.
- Multiple local learner profiles, ages 7-11, with separate results and a saved active learner.
- Honest progress: actual completed rounds, consecutive practice days and independent letter/building answers. Listening completion and speech-service matches are not labelled as reading mastery.
- Adjustable text size, spacing, background, audio speed and saved BM/English choice.
- Parent area for profile edits/deletion, text report export, sign-in, password reset and optional backup/restore.
- Rotation/recreation preserves the active question, selected syllables, hints and score. Completing an activity does not award twice after rotation.

## Data and migration

Profiles and results are saved in app-private preferences, separated by local mode and authenticated parent UID. An account's data stays on the device after sign-out; local mode shows a separate set of profiles. App removal/data clearing deletes device data. Export or make a verified backup first.

The previous guest garden's star/round totals migrate once. Original cloud child profiles can be imported through Settings > Parent account & backup > Import original child profiles. Original scores are not converted into mastery or new reward points. Old files are preserved under `legacy-source/`, outside Android's build source set. No old cloud data is deleted by the upgrade.

The activity log keeps 500 recent completed rounds; lifetime stars/rounds and independent answer totals stay on each profile. Independent means a letter/building answer was completed without a hint or incorrect attempt. Audio is allowed. Speaking/listening activities are excluded from that percentage.

## Optional Firebase

Enable Email/Password in Firebase Authentication and review/deploy the provided `firestore.rules` in your Firebase project. Rules have not been deployed by this source change. The app reports server failures instead of claiming backup success. The new backup uses `/users/{uid}/dyslearn/state` and is manual, for one active device at a time. Restoring requires confirmation and replaces this version's device state; it does not merge concurrent edits. Keep the server payload under 700 KB. A timed-out Firestore write may remain queued until the connection returns.

Registration uses email/password and password confirmation. Passwords are passed to Firebase Auth and never saved by KataBloom. The arithmetic parent gate prevents accidental edits; it is not a secure password lock. No microphone permission is requested by KataBloom: speech input is handled by the device's external recognizer. That provider may require a network and processes speech under its own terms.

## Audio and teaching scope

Install/download a Malay TTS voice on the device for word read-aloud. The audio-help button opens Android TTS settings if a suitable voice is unavailable. Written hints remain usable. Synthetic TTS reads whole words and does not provide educator-recorded isolated phonemes. Emoji cues vary by device and are decorative; they are not the only way to identify an answer.

This app is a learning prototype, not a dyslexia diagnosis, treatment or clinically validated assessment. Have a Malay literacy educator review the word bank, syllable divisions and difficulty before using it in an FYP study. Test speech with the intended accent/device. Follow school and guardian consent requirements for studies.

## Build and test

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug
```

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.

```powershell
.\gradlew.bat assembleQa assembleQaAndroidTest
```

QA uses the separate package `com.example.myproject.qa`, with no live Firebase account. Install the QA app and QA test APK on an emulator and run `com.example.myproject.qa.test/androidx.test.runner.AndroidJUnitRunner`. The tests refuse to clear preferences outside the QA package. They cover profile isolation, deletion, persistence, safe restore, language persistence, completing a round, rotation, retry/hint state and syllable selection. Use an emulator for these tests, not your real learner installation.

GitHub Actions builds/tests/lints each push to main and supplies a downloadable debug APK. It does not install on your device. Press Run in Android Studio for the fastest local update. GitHub-hosted debug builds use a different signing key from local builds unless you configure a persistent signing key; use your local build consistently on the tablet to preserve upgrade compatibility.

See VALIDATION.md for checks actually performed and remaining limitations.

## KataBloom branding and cloud checks

KataBloom is the app's new display name, formerly DysLearn. Install the build as an update to retain existing learner profiles. The Android Studio project is also named KataBloom.

The repository includes 12 Firestore security tests for family isolation, signed-out access, backup size/type/timestamp validation, profile access and read-only lesson content. Use Node 24 and JDK 21, then run:

```powershell
npm ci
npm run test:rules
```

The command uses the isolated demo-dyslearn project and local Firestore emulator. It refuses to run its tests without the emulator environment variable, requires no live Firebase account, and never deploys rules. The Check Firebase rules GitHub workflow runs when cloud rules/testing files change.

Live Firebase sign-in, backup/restore and logout still require a check with the owner's real test account and deployed rules.
