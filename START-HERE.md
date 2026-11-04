# DysLearn: improved FYP project

This is an edited copy of the original MyProject.zip, not a replacement app. Open this folder in Android Studio. The original ZIP remains unchanged.

## Try the new experience

Run the app and select **Explore the word garden**. No sign-in is required. Choose one of three gardens and a learning mode: listen and look, identify the first letter, or complete a word. Each round has five Malay words. Finish to receive five effort stars. Badges appear after 1, 3 and 10 completed rounds. There are no timers, lost lives or penalties for help. These are participation rewards, not evidence of mastery.

The garden also appears on the existing Learning screen. A selected child has separate local garden rewards; guest practice uses a shared guest record. Garden stars do not sync to Firestore or appear in the original cloud reports. Guest rewards are not transferred into child profiles.

Display preferences include larger text, extra letter spacing and a white background alternative to cream. Read-aloud uses the installed Malay Android TTS voice. Install the appropriate voice in Android settings if unavailable. Synthetic whole-word audio is not an isolated phoneme recording. Garden activities are in Bahasa Melayu; existing account screens remain in English.

## Repairs included

- Prevent repeated scoring on the legacy quiz question screen and speech screen.
- Prevent legacy quiz navigation before answering and after completion.
- Reset quiz option colours between questions.
- Show the actual answer text, rather than only its option label, after an incorrect quiz answer.
- Report failures to save legacy quiz progress to Firebase.
- Remove the manifest declaration for the nonexistent QuizQuestions activity.
- Award the old reward-engine streak bonus at each third correct answer, rather than every answer after the third.
- Replace the speech badge's pronunciation-mastery claim with a participation label.
- Add a scrollable landing screen and an offline entry point.
- Preserve garden question, help and score state across activity recreation; prevent duplicate completion rewards on restored rounds.

## Build

Use Android Studio's bundled JDK and installed SDK 36. Existing Firebase configuration from the original archive is retained. The configured Firebase backend, authentication settings, deployed security rules and content collections must be reviewed in the owner's Firebase console; compilation does not verify them.

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest lintDebug
```

The project still contains several overlapping original AI/learning implementations. They have not been presented as clinically validated AI or a dyslexia assessment. Existing phonics, speech and quiz screens still depend on the original Firestore content. The new garden is the bundled offline learning path.

## Before your FYP user study

1. Have a Malay primary literacy educator check word selection, syllable divisions and difficulty.
2. Test on a small phone, large Android font setting, TalkBack, landscape, rotation, and a device without Malay TTS.
3. Complete a garden round with incorrect answers and hints. Check that exactly five stars are saved, rotation does not duplicate them, and different child profiles have separate rewards.
4. Test sign-in, child selection, cloud permissions and report saving with a dedicated test Firebase account. Do not publish children's real records in screenshots or sample data.
5. Evaluate usability with appropriate school/guardian consent. Collect task completion, requests for help and learner feedback. Do not describe stars or speech-recognition matches as measured learning improvement.

Display design reference: British Dyslexia Association, Dyslexia Style Guide (2023): https://cdn.bdadyslexia.org.uk/uploads/documents/Advice/style-guide/BDA-Style-Guide-2023.pdf . Preferences support individual comfort; they do not treat dyslexia.
