# SNAIL Messenger — Testing Notes

## Static verification checklist

- [ ] Build debug variant with Gradle in Android Studio.
- [ ] Register two local demo accounts and verify duplicate usernames are rejected.
- [ ] Sign in/out and confirm local account persistence.
- [ ] Send text, edit your own text, delete a message, add/remove a reaction, reply, forward between local chats, create/vote in a poll, and search within a chat.
- [ ] Pick multiple photos/videos and documents (up to 10 per action), add a caption, and verify attachment ordering and persistence.
- [ ] Record a voice message, cancel it by navigating away while recording, and verify no partial recording is sent.
- [ ] Test light, dark, and system themes, chat wallpapers, and bottom navigation across small and large screens.
- [ ] Open an unread chat and verify its inbox badge clears; mute/unmute a chat from the row and profile sheet and verify state persists.
- [ ] Exercise voice/video call screens as UI prototypes; verify no claim of real remote connectivity.
- [ ] Test URI permissions after Android process death and app restart on API 28+ devices.

## Current verification status

The latest source changes are committed, but an Android Gradle compile and on-device run have not been performed in this pass. Treat picker URI persistence as needing device verification. Cloud messaging, cross-device accounts, backend presence, end-to-end encryption, push delivery, and real voice/video calling are not implemented by the local prototype.
