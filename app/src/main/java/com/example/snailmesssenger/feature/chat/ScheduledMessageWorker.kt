package com.example.snailmesssenger.feature.chat

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.snailmesssenger.data.repository.DemoAuthRepository
import com.example.snailmesssenger.data.repository.DemoMessagingRepository
import java.util.UUID
import java.util.concurrent.TimeUnit
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
        val conversationId = inputData.getString(KEY_CONVERSATION_ID) ?: return Result.failure()
        val text = inputData.getString(KEY_TEXT)?.trim().orEmpty()
        val scheduledId = inputData.getString(KEY_SCHEDULED_ID) ?: id.toString()
        val replyToMessageId = inputData.getString(KEY_REPLY_TO_MESSAGE_ID)
        val replyToText = inputData.getString(KEY_REPLY_TO_TEXT)
        if (text.isBlank()) return Result.failure()

        return try {
            DemoAuthRepository.initialize(applicationContext)
            DemoMessagingRepository.initialize(applicationContext, DemoAuthRepository.currentUser?.id)
            DemoMessagingRepository.sendScheduledMessage(
                conversationId = conversationId,
                text = text,
                scheduledId = scheduledId,
                replyToMessageId = replyToMessageId,
                replyToText = replyToText,
            )
            Result.success()
        } catch (_: IllegalArgumentException) {
            Result.failure()
        } catch (_: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val SCHEDULED_TAG = "snail_scheduled_message"
        private const val KEY_CONVERSATION_ID = "conversation_id"
        private const val KEY_TEXT = "text"
        private const val KEY_SCHEDULED_ID = "scheduled_id"
        private const val KEY_SCHEDULED_AT = "scheduled_at"
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
            require(normalizedText.length <= 4_000) { "Scheduled messages are limited to 4,000 characters." }
            require(scheduledAtMs > System.currentTimeMillis()) { "Choose a future date and time." }

            val scheduledId = UUID.randomUUID().toString()
            val data = workDataOf(
                KEY_CONVERSATION_ID to conversationId,
                KEY_TEXT to normalizedText,
                KEY_SCHEDULED_ID to scheduledId,
                KEY_SCHEDULED_AT to scheduledAtMs,
                KEY_REPLY_TO_MESSAGE_ID to replyToMessageId,
                KEY_REPLY_TO_TEXT to replyToText?.take(2_000),
            )
            val request = OneTimeWorkRequestBuilder<ScheduledMessageWorker>()
                .setInputData(data)
                .setInitialDelay(scheduledAtMs - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addTag(SCHEDULED_TAG)
                .build()
            WorkManager.getInstance(context.applicationContext).enqueue(request)
            return request.id
        }

        suspend fun pendingForConversation(
            context: Context,
            conversationId: String,
        ): List<ScheduledMessageItem> = withContext(Dispatchers.IO) {
            WorkManager.getInstance(context.applicationContext)
                .getWorkInfosByTag(SCHEDULED_TAG)
                .get()
                .asSequence()
                .filter { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.BLOCKED }
                .mapNotNull { info ->
                    val data: Data = info.inputData
                    val targetConversationId = data.getString(KEY_CONVERSATION_ID) ?: return@mapNotNull null
                    if (targetConversationId != conversationId) return@mapNotNull null
                    val text = data.getString(KEY_TEXT) ?: return@mapNotNull null
                    val scheduledId = data.getString(KEY_SCHEDULED_ID) ?: info.id.toString()
                    val scheduledAt = data.getLong(KEY_SCHEDULED_AT, 0L)
                    if (scheduledAt <= 0L) return@mapNotNull null
                    ScheduledMessageItem(
                        workRequestId = info.id,
                        scheduledId = scheduledId,
                        conversationId = targetConversationId,
                        text = text,
                        scheduledAtMs = scheduledAt,
                    )
                }
                .sortedBy { it.scheduledAtMs }
                .toList()
        }

        fun cancel(context: Context, workRequestId: UUID) {
            WorkManager.getInstance(context.applicationContext).cancelWorkById(workRequestId)
        }
    }
}
