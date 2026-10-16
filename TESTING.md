# SNAIL Messenger — Testing Notes

## Static verification checklist

- [ ] Build debug variant with Gradle in Android Studio.
- [ ] Register two local demo accounts and verify duplicate usernames are rejected.
- [ ] Sign in/out and confirm local account persistence.
- [ ] Send text, edit your own text, delete a message, add/remove a reaction, reply, forward between local chats, create/vote in a poll, and search within a chat.
- [ ] Search for text that exists only in an older message; verify it appears under the global MESSAGES section, opens the correct conversation, and scrolls/highlights the exact message.
- [ ] Pin a message, verify the banner survives relaunch and jumps to the right item, then unpin it.
- [ ] Type a draft and reply, leave the chat immediately (including before the debounce delay), return and restart the app; confirm text/reply draft restores and clears after sending.
- [ ] Schedule a message for the future, reopen the scheduled list, cancel another scheduled message, and verify a due message appears once after waiting/restarting.
- [ ] Open Saved Messages twice and confirm only one self-chat exists and its messages persist after app restart.
- [ ] Create a group/channel, reopen its profile, add/remove members or subscribers, and verify membership count and selected list update and persist.
- [ ] Pick multiple photos/videos and documents (up to 10 per action), add a caption, and verify attachment ordering and persistence.
- [ ] Record a voice message, cancel it by navigating away while recording, and verify the latest recorder is stopped/released and no partial recording is sent.
- [ ] Test light, dark, and system themes, chat wallpapers, and bottom navigation across small and large screens.
- [ ] Open an unread chat and verify its inbox badge clears; mute/unmute a chat from the row, profile sheet and chat overflow; pin/unpin in the chat overflow.
- [ ] Export a chat via Android Sharesheet, then clear history and confirm the empty conversation persists after relaunch and its pinned banner disappears.
- [ ] Delete a pinned message; confirm the delete dialog appears and the pinned banner is removed.
- [ ] Exercise voice/video call screens as UI prototypes; verify no claim of real remote connectivity.
- [ ] Test URI permissions after Android process death and app restart on API 28+ devices.

## Current verification status

The latest source changes are committed, but an Android Gradle compile and on-device run have not been performed in this pass. Treat picker URI persistence as needing device verification. Cloud messaging, cross-device accounts, backend presence, end-to-end encryption, push delivery, and real voice/video calling are not implemented by the local prototype.
