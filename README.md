# SNAIL Messenger

SNAIL is a native Android messaging prototype built with Kotlin and Jetpack Compose. It uses original SNAIL branding and an adaptive, glass-layered interface inspired by contemporary messenger interaction patterns.

## Current app surfaces

- Username/password sign-up and sign-in for device-local demo accounts (passwords are salted and PBKDF2-hashed).
- One-to-one conversation list, search, unread/work/personal filters, contacts lookup, theme and profile settings.
- Text messages stored on the device, editable/deletable outgoing text, emoji reactions, and message search.
- The signed-in local demo account is restored after app restart; logout clears that local session.
- Attach photos/videos through Android's system media picker and documents through the system document picker.
- Local message/attachment metadata persistence, adjustable chat wallpaper, light/dark modes, and responsive navigation.
- Voice/video call screens and media-selection UI are present as prototype surfaces; they do not yet establish real network calls or upload/sync media.

## Important limitations

The current repositories are local prototype stores. Accounts and messages do not synchronize between users or devices, and messages are not end-to-end encrypted. Presence/read receipts are UI/demo state, not a server-backed delivery guarantee. The app does not yet provide a production messaging backend, push notifications, multi-device sync, or actual WebRTC calling.

The UI borrows broad usability patterns from modern messaging applications while keeping SNAIL's own names and assets. It does not copy Telegram's source code or brand assets.

## Build

Open the project in a compatible Android Studio/Gradle environment and run the app configuration. See TESTING.md for the current verification checklist.
