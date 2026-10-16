package com.example.snailmesssenger.feature.chat

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.snailmesssenger.data.repository.DemoAuthRepository
import com.example.snailmesssenger.data.repository.DemoMessagingRepository
import com.example.snailmesssenger.data.security.LocalVault
import java.util.UUID
import java.util.concurrent.TimeUnit
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ScheduledMessageItem(
    val workRequestId: UUID,
    val scheduledId: String,
    val conversationId: String,
    val text: String,
    val scheduledAtMs: Long,
)

class ScheduledMessageWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val scheduledAccountId = inputData.getString(KEY_ACCOUNT_ID) ?: "guest"
        val conversationId = inputData.getString(KEY_CONVERSATION_ID)
        val text = runCatching { LocalVault.decrypt(inputData.getString(KEY_TEXT).orEmpty()) }
            .getOrNull()?.trim().orEmpty()
        val scheduledId = inputData.getString(KEY_SCHEDULED_ID) ?: id.toString()
        val replyToMessageId = inputData.getString(KEY_REPLY_TO_MESSAGE_ID)
        val replyToText = inputData.getString(KEY_REPLY_TO_TEXT)
            ?.let { runCatching { LocalVault.decrypt(it) }.getOrNull() }

        if (conversationId.isNullOrBlank() || text.isBlank()) {
            removeScheduledMetadata(applicationContext, id, scheduledAccountId)
            return Result.failure()
        }

        return try {
            DemoAuthRepository.initialize(applicationContext)
            val activeAccountId = DemoAuthRepository.currentUser?.id ?: "guest"
            if (activeAccountId != scheduledAccountId) {
                removeScheduledMetadata(applicationContext, id, scheduledAccountId)
                return Result.failure()
            }
            DemoMessagingRepository.initialize(applicationContext, DemoAuthRepository.currentUser?.id)
            DemoMessagingRepository.sendScheduledMessage(
                conversationId = conversationId,
                text = text,
                scheduledId = scheduledId,
                replyToMessageId = replyToMessageId,
                replyToText = replyToText,
            )
            removeScheduledMetadata(applicationContext, id, scheduledAccountId)
            Result.success()
        } catch (_: IllegalArgumentException) {
            removeScheduledMetadata(applicationContext, id, scheduledAccountId)
            Result.failure()
        } catch (_: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                removeScheduledMetadata(applicationContext, id, scheduledAccountId)
                Result.failure()
            }
        }
    }

    companion object {
        const val SCHEDULED_TAG = "snail_scheduled_message"
        private const val KEY_CONVERSATION_ID = "conversation_id"
        private const val KEY_TEXT = "text"
        private const val KEY_SCHEDULED_ID = "scheduled_id"
        private const val KEY_SCHEDULED_AT = "scheduled_at"
        private const val KEY_ACCOUNT_ID = "account_id"
        private const val KEY_REPLY_TO_MESSAGE_ID = "reply_to_message_id"
        private const val KEY_REPLY_TO_TEXT = "reply_to_text"

        fun enqueue(
            context: Context,
            conversationId: String,
            text: String,
            scheduledAtMs: Long,
            replyToMessageId: String? = null,
            replyToText: String? = null,
        ): UUID {
            val normalizedText = text.trim()
            require(normalizedText.isNotEmpty()) { "Message cannot be empty." }
            require(normalizedText.length <= 1_500) { "Scheduled messages are limited to 1,500 characters." }
            require(scheduledAtMs > System.currentTimeMillis()) { "Choose a future date and time." }

            val scheduledId = UUID.randomUUID().toString()
            val data = workDataOf(
                KEY_CONVERSATION_ID to conversationId,
                KEY_TEXT to LocalVault.encrypt(normalizedText),
                KEY_SCHEDULED_ID to scheduledId,
                KEY_SCHEDULED_AT to scheduledAtMs,
                KEY_ACCOUNT_ID to (DemoAuthRepository.currentUser?.id ?: "guest"),
                KEY_REPLY_TO_MESSAGE_ID to replyToMessageId,
                KEY_REPLY_TO_TEXT to replyToText?.take(300)?.let(LocalVault::encrypt),
            )
            val request = OneTimeWorkRequestBuilder<ScheduledMessageWorker>()
                .setInputData(data)
                .setInitialDelay(scheduledAtMs - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addTag(SCHEDULED_TAG)
                .build()
            val accountId = DemoAuthRepository.currentUser?.id ?: "guest"
            persistScheduledMetadata(
                context = context,
                workRequestId = request.id,
                accountId = accountId,
                conversationId = conversationId,
                scheduledId = scheduledId,
                text = normalizedText,
                scheduledAtMs = scheduledAtMs,
            )
            WorkManager.getInstance(context.applicationContext).enqueue(request)
            return request.id
        }

        suspend fun pendingForConversation(
            context: Context,
            conversationId: String,
        ): List<ScheduledMessageItem> = withContext(Dispatchers.IO) {
            DemoAuthRepository.initialize(context.applicationContext)
            val activeAccountId = DemoAuthRepository.currentUser?.id ?: "guest"
            WorkManager.getInstance(context.applicationContext)
                .getWorkInfosByTag(SCHEDULED_TAG)
                .get()
                .asSequence()
                .filter { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.BLOCKED }
                .mapNotNull { info ->
                    val item = readScheduledMetadata(context, info.id, activeAccountId) ?: return@mapNotNull null
                    if (item.conversationId != conversationId) return@mapNotNull null
                    item
                }
                .sortedBy { it.scheduledAtMs }
                .toList()
        }

        fun cancel(context: Context, workRequestId: UUID) {
            WorkManager.getInstance(context.applicationContext).cancelWorkById(workRequestId)
            val accountId = DemoAuthRepository.currentUser?.id ?: "guest"
            removeScheduledMetadata(context.applicationContext, workRequestId, accountId)
        }

        private const val METADATA_PREFS_PREFIX = "snail_scheduled_metadata_"

        private fun metadataStore(context: Context, accountId: String): SharedPreferences {
            val safeKey = accountId.filter { it.isLetterOrDigit() || it == '_' || it == '-' }
                .take(48)
                .ifBlank { "guest" }
            return context.applicationContext.getSharedPreferences(
                "$METADATA_PREFS_PREFIX$safeKey",
                MODE_PRIVATE,
            )
        }

        private fun persistScheduledMetadata(
            context: Context,
            workRequestId: UUID,
            accountId: String,
            conversationId: String,
            scheduledId: String,
            text: String,
            scheduledAtMs: Long,
        ) {
            val payload = JSONObject()
                .put("accountId", accountId)
                .put("conversationId", conversationId)
                .put("scheduledId", scheduledId)
                .put("text", text)
                .put("scheduledAtMs", scheduledAtMs)
            metadataStore(context, accountId)
                .edit()
                .putString(workRequestId.toString(), LocalVault.encrypt(payload.toString()))
                .apply()
        }

        private fun readScheduledMetadata(
            context: Context,
            workRequestId: UUID,
            accountId: String,
        ): ScheduledMessageItem? {
            val stored = metadataStore(context, accountId).getString(workRequestId.toString(), null)
                ?: return null
            val raw = runCatching { LocalVault.decrypt(stored) }.getOrNull() ?: return null
            val payload = runCatching { JSONObject(raw) }.getOrNull() ?: return null
            if (payload.optString("accountId") != accountId) return null
            val conversationId = payload.optString("conversationId").takeIf { it.isNotBlank() } ?: return null
            val scheduledId = payload.optString("scheduledId").takeIf { it.isNotBlank() } ?: return null
            val text = payload.optString("text").takeIf { it.isNotBlank() } ?: return null
            val scheduledAtMs = payload.optLong("scheduledAtMs", 0L).takeIf { it > 0L } ?: return null
            return ScheduledMessageItem(
                workRequestId = workRequestId,
                scheduledId = scheduledId,
                conversationId = conversationId,
                text = text,
                scheduledAtMs = scheduledAtMs,
            )
        }

        private fun removeScheduledMetadata(context: Context, workRequestId: UUID, accountId: String) {
            metadataStore(context, accountId).edit().remove(workRequestId.toString()).apply()
        }
    }
}
