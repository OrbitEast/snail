# SNAIL Messenger

SNAIL is a native Android messaging prototype built with Kotlin and Jetpack Compose. It uses original SNAIL branding and an adaptive, glass-layered interface inspired by contemporary messenger interaction patterns.

## Current app surfaces

- Username/password sign-up and sign-in for device-local demo accounts (passwords are salted and PBKDF2-hashed).
- One-to-one chats plus local group/channel creation, search across conversations and message history, unread/pinned/archive filters, contacts lookup, theme and profile settings.
- Text messages stored on the device, editable/deletable outgoing text, reply quotes, forwarding between local conversations, emoji reactions, local polls with vote counts, pinned messages with a jump banner, full-text search, and per-conversation drafts that survive navigation and app restart.
- Groups and broadcast-style channels can be created on-device and their membership metadata persists locally; they do not yet communicate with remote members.
- Opening a chat clears its local unread count. Chat overflow supports persistent pin/mute, plain-text export via Android Sharesheet, and confirmed local history clearing.
- The signed-in local demo account is restored after app restart; logout clears that local session.
- Select up to 10 photos/videos or documents per composer action, add a caption before sending, tap images for full-screen preview, or open video/files/audio in another installed app.
- Record voice-message audio locally with a microphone permission prompt, a live duration counter, and a dedicated in-app private-storage file URI.
- Invite links can be shared through Android's system Sharesheet.
- Local message/attachment metadata persistence, adjustable chat wallpaper, light/dark modes, and responsive navigation.
- Voice/video call screens remain prototypes and do not establish real network calls. Voice messages are recorded and stored locally; they are not uploaded or delivered to another user.

## Important limitations

The current repositories are local prototype stores. Accounts and messages do not synchronize between users or devices; poll votes are local-only, and messages are not end-to-end encrypted. Presence/read receipts are UI/demo state, not a server-backed delivery guarantee. The app does not yet provide a production messaging backend, push notifications, multi-device sync, or actual WebRTC calling.

The UI borrows broad usability patterns from modern messaging applications while keeping SNAIL's own names and assets. It does not copy Telegram's source code or brand assets.

## Build

Open the project in a compatible Android Studio/Gradle environment and run the app configuration. See TESTING.md for the current verification checklist.
