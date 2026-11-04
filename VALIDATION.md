# DysLearn 2.1 validation

- Debug, isolated QA app and QA instrumentation APKs built successfully with SDK 36 and JDK 21.
- 6 learning-logic unit tests passed: word/syllable consistency, choice uniqueness, score-once behavior, guided attempts, normalized speech matching, streak dates and invalid restored positions.
- 11 emulator tests passed, including profile persistence and isolation, deletion, invalid-restore rollback, backup round-trip, language persistence, completing/recreating rounds without duplicate rewards, syllable retry state and learner form entry.
- Final suite ran at 360 x 640 dp. Core learning UI also passed earlier on the original 1080 x 2400 emulator display. Display overrides were restored afterwards.
- Screenshots inspected for the welcome screen, home, lessons, initial-letter exercise, word building, completion and larger-text English settings. No physical tablet was modified by QA tests.
- Android lint: 0 errors and 9 warnings, reduced from 453 warnings in the previous version. Remaining warnings are dependency/version-catalog suggestions. No baseline or lint suppression was added.
- Debug APK size: 11.55 MB, previously about 52 MB.
- 159 retired source/resource files were verified preserved under legacy-source, outside Android's active source set.

## New interface checks

Landing sign-in/sign-up entry, return-to-welcome session exit, the seven-day activity chart, and the independent-answer ring were included in the QA navigation and screenshot checks. Real Firebase authentication/logout still requires a live-account check.

## Remaining checks

Live Firebase sign-in, password reset, account ownership rules, backup/restore and original cloud-profile import require testing against the owner's configured Firebase backend. The provided rules have not been deployed. Real Malay voice quality, external speech recognition, TalkBack and the user's physical tablet still need device checks. Content and learning suitability need review by a Malay literacy educator; no clinical validation is claimed. Landscape and every device/font combination have not been exhaustively tested.

## APK identity

Version: 2.1 (code 3)

SHA-256: `48e0cdbc9ec3d114b5a6c68bd79578f8cf4ce2417fefd73be82c3116abec528a`
