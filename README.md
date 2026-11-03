# LEGION Control Center (starter Android project)

A native Android starter project for a landscape, red/black Legion-inspired control panel.

## What is included
- Landscape control panel UI with gothic red/black styling.
- Character picker for transparent PNG/WebP or other image files.
- Character image URI persisted between launches.
- Drag the character around within the left panel.
- Brightness and media-volume sliders (brightness requires the user to grant Modify system settings).
- Quick Settings tile that launches the custom panel.
- Wi-Fi/Bluetooth/Internet settings shortcuts, location/settings shortcuts, and flashlight toggle.

## Important Android limitation
A normal third-party APK cannot replace or suppress Android/ZUI's real System UI Quick Settings panel or reliably take over the system status-bar swipe. This project exposes a Quick Settings tile and a full-screen custom panel instead. OEM accessibility apps may use device-specific mechanisms; those cannot be assumed to work on every ZUXOS build. Screen recording, airplane mode, dark mode, and some radios are routed to system settings or left as guidance because Android restricts direct toggling for ordinary apps.

## Build
1. Open this folder in Android Studio (Hedgehog or newer).
2. Let Android Studio install the Android SDK Platform 35 and Gradle plugin dependencies.
3. Build > Build Bundle(s) / APK(s) > Build APK(s).
4. Install the debug APK on the Lenovo Legion Y700.
5. Add the `LEGION Control Center` tile from the system Quick Settings editor.

## Character requirements
Use a transparent PNG or WebP for best results. The app also accepts other image formats, but an opaque background will remain visible.
