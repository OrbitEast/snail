# SNAIL Messenger — Testing Notes

## Static verification checklist

- [ ] Build debug variant with Gradle in Android Studio.
- [ ] Register two local demo accounts and verify duplicate usernames are rejected.
- [ ] Inspect app preferences: conversation JSON, drafts and blocklist should begin with the SNAIL encrypted-payload prefix, and Android backup should be disabled.
- [ ] Enable app passcode, lock immediately, verify incorrect PINs are rate-limited, unlock with the correct PIN, and verify backgrounding for over 30 seconds triggers the lock.
- [ ] Sign in/out and confirm local account persistence.
- [ ] Register two different local accounts, add different messages/drafts in each, switch accounts, and verify one account never sees the other's local conversations or drafts.
- [ ] Upgrade once from the previous shared-store version and verify legacy data is migrated only into the first active account, then removed from the legacy preference file.
- [ ] Send text, edit your own text, delete a message, add/remove a reaction, reply, forward between local chats, create/vote in a poll, and search within a chat.
- [ ] Search for text that exists only in an older message; verify it appears under the global MESSAGES section, opens the correct conversation, and scrolls/highlights the exact message.
- [ ] Pin a message, verify the banner survives relaunch and jumps to the right item, then unpin it.
- [ ] Type a draft and reply, leave the chat immediately (including before the debounce delay), return and restart the app; confirm text/reply draft restores and clears after sending.
- [ ] Schedule a message for the future, reopen the scheduled list, cancel another scheduled message, and verify a due message appears once after waiting/restarting.
- [ ] Verify pending schedules render correctly from the encrypted metadata index, cancel removes the index entry, and completed jobs remove their metadata.
- [ ] Schedule under one local account, switch/logout before the due time, and verify the job cannot place that account's text into another account's local history.
- [ ] Open Saved Messages twice and confirm only one self-chat exists and its messages persist after app restart.
- [ ] Create a group/channel, reopen its profile, add/remove members or subscribers, and verify membership count and selected list update and persist.
- [ ] Pick multiple photos/videos and documents (up to 10 per action), add a caption, and verify attachment ordering and persistence.
- [ ] Record a voice message, cancel it by navigating away while recording, and verify the latest recorder is stopped/released and no partial recording is sent.
- [ ] Test light, dark, and system themes, chat wallpapers, and bottom navigation across small and large screens.
- [ ] Open an unread chat and verify its inbox badge clears; mute/unmute a chat from the row, profile sheet and chat overflow; pin/unpin in the chat overflow.
- [ ] Block a direct-chat contact from the profile; verify the composer and attachment/voice actions disable, blocked-users settings shows the contact, and unblocking restores local sending controls.
- [ ] Export a chat via Android Sharesheet, then clear history and confirm the empty conversation persists after relaunch and its pinned banner disappears.
- [ ] Delete a pinned message; confirm the delete dialog appears and the pinned banner is removed.
- [ ] Exercise voice/video call screens as UI prototypes; verify no claim of real remote connectivity.
- [ ] Test URI permissions after Android process death and app restart on API 28+ devices.

## Current verification status

The latest source changes are committed, but an Android Gradle compile and on-device run have not been performed in this pass. Treat picker URI persistence as needing device verification. Cloud messaging, cross-device accounts, backend presence, end-to-end encryption, push delivery, and real voice/video calling are not implemented by the local prototype.
