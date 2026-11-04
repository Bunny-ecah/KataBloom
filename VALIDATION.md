# DysLearn 2.0 validation

- Debug, isolated QA app and QA instrumentation APKs built successfully with SDK 36 and JDK 21.
- 6 learning-logic unit tests passed: word/syllable consistency, choice uniqueness, score-once behavior, guided attempts, normalized speech matching, streak dates and invalid restored positions.
- 10 emulator tests passed, including profile persistence and isolation, deletion, invalid-restore rollback, backup round-trip, language persistence, completing/recreating rounds without duplicate rewards, syllable retry state and learner form entry.
- Final suite ran at 360 x 640 dp. Core learning UI also passed earlier on the original 1080 x 2400 emulator display. Display overrides were restored afterwards.
- Screenshots inspected for the welcome screen, home, lessons, initial-letter exercise, word building, completion and larger-text English settings. No physical tablet was modified by QA tests.
- Android lint: 0 errors and 9 warnings, reduced from 453 warnings in the previous version. Remaining warnings are dependency/version-catalog suggestions. No baseline or lint suppression was added.
- Debug APK size: 11.50 MB, previously about 52 MB.
- 159 retired source/resource files were verified preserved under legacy-source, outside Android's active source set.

## Remaining checks

Live Firebase sign-in, password reset, account ownership rules, backup/restore and original cloud-profile import require testing against the owner's configured Firebase backend. The provided rules have not been deployed. Real Malay voice quality, external speech recognition, TalkBack and the user's physical tablet still need device checks. Content and learning suitability need review by a Malay literacy educator; no clinical validation is claimed. Landscape and every device/font combination have not been exhaustively tested.

## APK identity

Version: 2.0 (code 2)

SHA-256: `2212b16c961cda9ba42c434af5b73cc816daa0b7d5cbfab1aaebdc633e268910`
