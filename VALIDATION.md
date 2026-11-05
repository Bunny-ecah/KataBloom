# KataBloom 2.2.1 validation

- APK, isolated QA app and QA instrumentation packages built successfully with JDK 21 / SDK 36.
- APK metadata verified: app label KataBloom, versionName 2.2.1, versionCode 5, applicationId com.example.myproject.
- 6 Android learning-logic unit tests passed on the renamed build.
- 12 Firestore emulator tests passed: owner-only backup access, signed-out denial, unexpected fields, non-string/oversized payloads, server timestamps, updates, deletion, child-profile isolation and read-only lesson content. Test project demo-katabloom has no live data.
- The previous 2.1 build passed all 11 mobile instrumentation tests at 360 x 640 dp, including landing/account navigation and session exit. The new branding assertion compiles but could not run because the emulator disconnected. Name verification used Android APK metadata instead.
- Lint: 0 errors and 9 advisory warnings. No baseline/suppression added.
- Original app/data identifiers are retained for in-place updates. Visible app/project/report branding is KataBloom. The active screen class is KataBloomActivity; testing-tool package and emulator-project names also use KataBloom. Historical source and compatibility identifiers retain their original names.
- Debug APK size: 11.46 MB.

## Remaining manual checks

Live Firebase email registration/sign-in, password reset, backup/restore and logout need the owner's real test account and deployed rules. The rules were tested locally and were not deployed. Private GitHub Actions status could not be read from this session. Check the repository's Actions tab for the published run. A Malay literacy educator should review learning content; real device voice availability, recognition quality and accessibility still need device checks.

APK SHA-256: `1e505859830a43d1fba890bd94c1d3b443bd31435d56d7d9b2acab5e8e3b9c06`
