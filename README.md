# SNAIL Messenger

SNAIL is a native Android messaging prototype built with Kotlin and Jetpack Compose. It uses original SNAIL branding and an adaptive, glass-layered interface inspired by contemporary messenger interaction patterns.

## Current app surfaces

- Username/password sign-up and sign-in for device-local demo accounts (passwords are salted and PBKDF2-hashed).
- One-to-one chats, a persistent Saved Messages self-chat, local group/channel creation, search across conversations and message history with direct navigation to the matching message, unread/pinned/archive filters, contacts lookup, theme and profile settings.
- Text messages stored on the device, editable/deletable outgoing text, reply quotes, forwarding between local conversations, emoji reactions, local polls with vote counts, pinned messages with a jump banner, full-text search, per-conversation drafts, and scheduled local messages with cancellation.
- Groups and broadcast-style channels can be created on-device; member/subscriber lists can be edited and persist locally, but do not synchronize with remote members.
- Opening a chat clears its local unread count. Chat overflow supports persistent pin/mute, plain-text export via Android Sharesheet, and confirmed local history clearing. Individual message deletion also requires confirmation and clears stale pinned-message references.
- Block/unblock contacts locally from profiles; blocked IDs are encrypted in the active account store and prevent sending messages, polls, or attachments to that direct chat on this device.
- The signed-in local demo account is restored after app restart; local conversations and drafts are isolated by account, and logout clears that session.
- Conversation JSON, drafts, and scheduled message payloads are encrypted at rest with an Android Keystore AES-GCM key; Android app-data backup is disabled. This is not end-to-end encryption.
- Select up to 10 photos/videos or documents per composer action, add a caption before sending, tap images for full-screen preview, or open video/files/audio in another installed app.
- Record voice-message audio locally with a microphone permission prompt, a live duration counter, and a dedicated in-app private-storage file URI.
- Invite links can be shared through Android's system Sharesheet.
- Local message/attachment metadata persistence, adjustable chat wallpaper, light/dark modes, and responsive navigation.
- Scheduled messages use persistent Android WorkManager jobs with encrypted payloads and are appended to the matching account's local conversation at the due time; they are not delivered to a remote recipient and are discarded if that account is no longer the active local session. Android power-saving can delay execution.
- Voice/video call screens remain prototypes and do not establish real network calls. Voice messages are recorded and stored locally; they are not uploaded or delivered to another user.

## Important limitations

The current repositories are local prototype stores. Accounts and messages do not synchronize between users or devices; local data is separated between accounts on this device, poll votes are local-only, and messages are not end-to-end encrypted. Presence/read receipts are UI/demo state, not a server-backed delivery guarantee. The app does not yet provide a production messaging backend, push notifications, multi-device sync, or actual WebRTC calling.

The UI borrows broad usability patterns from modern messaging applications while keeping SNAIL's own names and assets. It does not copy Telegram's source code or brand assets.

## Build

Open the project in a compatible Android Studio/Gradle environment and run the app configuration. See TESTING.md for the current verification checklist.
