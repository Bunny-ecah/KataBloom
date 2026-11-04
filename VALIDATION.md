# Validation

Final debug APK assembled successfully with SDK 36 and Android Studio JDK 21.
Android lint: 0 errors; 453 warnings remain (including hardcoded text, deprecated APIs and legacy layout/accessibility warnings). No lint baseline or error suppression was added.
Unit tests: 4 passed. Includes choice uniqueness, correct-answer positions, streak bonus boundaries, and missing profile-map fallback.

Not performed: emulator/physical-device visual and interaction testing, real Firebase authentication/database integration, or educator/user-study validation. See START-HERE.md for the manual checks.

Additional repairs after lint: malformed color drawable, AppCompat tint namespace, and API 23 compatibility for map lookups. Missing nested profile maps now use fallback values.
