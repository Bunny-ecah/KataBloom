# Start here

1. Open this folder in Android Studio (the folder containing settings.gradle.kts).
2. Wait for sync, select your tablet, and press Run.
3. The welcome page offers Sign in, Sign up and Continue without an account. Choose local learning to add a learner or try a guest round.
4. Use English / BM at the top to change the interface language.
5. Choose Learn, select a garden and one of four activity types.
6. Complete five words to save a round and receive five effort stars.
7. Progress shows a seven-day chart, an answer ring and the badge trail. Settings contains reading preferences, parent account options, and Sign out (or Return to welcome in local mode).

The new version replaces the former mix of screens. Original source/resources are preserved in legacy-source for reference. Guest garden totals migrate once; importing original Firebase child profiles is an explicit parent action. Existing cloud records remain unchanged.

To share the app with other people, publish a signed release (see README > Publishing a release); they install it from the Releases page. Android Studio Run builds use your debug key and cannot update a Releases install, so test them on a separate device or uninstall first.

Read README.md for Firebase setup, migration, audio requirements and the scope of the FYP prototype. See VALIDATION.md for test results.
