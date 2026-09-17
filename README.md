# PhotoTracker — 90 Day Transformation

A simple Android app for a 90-day physical transformation photo tracker, based on the linked Figma design.

## How it works

- **Grid screen** — a 7-column grid of 90 numbered day slots. Tapping a slot opens that day's page.
- **Day page** — shows a placeholder card ("Tap to add your day N photo"). Tapping it opens the device camera. The captured photo is saved for that day.
- Going back to the grid, the day's slot now shows a small rotated "sticker" thumbnail of the photo instead of the number, building up a scrapbook-style collage of the 90 days.
- Tapping a day again lets you retake the photo.
- The info (ⓘ) icon explains the app; the refresh icon resets all captured photos after confirmation.

## Tech

- Kotlin + Jetpack Compose, single-activity, Navigation-Compose between the grid and day-detail screens.
- No backend — photos are saved to the app's private storage (`filesDir/photos/day_N.jpg`) via `FileProvider` + the system camera app (`ActivityResultContracts.TakePicture`). Whether a day is "done" is just derived from whether that file exists.
- Coil for image loading (with caching disabled for these files, since retaking a photo overwrites the same path and a stale cache would keep showing the old shot).
- minSdk 26, compileSdk/targetSdk 34.

## Building

Open the project root in Android Studio (Koala+) and let it sync — it will fetch the Android Gradle Plugin, Compose, and Coil dependencies automatically. Then Run on a device/emulator.

> Note: this project was scaffolded in a sandboxed cloud session without access to the Android SDK or Google's Maven repo, so it could not be compiled/run here. The code was written and reviewed carefully, but hasn't been build-verified — please flag anything Android Studio's sync/build turns up.

## Possible follow-ups

- Swap the "Day N" label font for a handwritten/script font (e.g. Google Fonts "Caveat") to match the Figma design more closely.
- Add the scalloped stamp/postage-stamp edge from the mockup (currently a plain rounded card).
- Add a "share collage" export once all 90 days are filled in.
