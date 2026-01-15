package com.example.quotevault.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.quotevault.R
import com.example.quotevault.data.remote.SupabaseProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DailyQuoteWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Fetch one quote (latest / random)
            val quote = SupabaseProvider.client
                .from("quotes")
                .select {
                    order("created_at", Order.DESCENDING)
                    limit(1)
                }
                .decodeSingle<Map<String, Any>>()

            val content = quote["content"]?.toString() ?: "Stay inspired ✨"
            val author = quote["author"]?.toString() ?: "QuoteVault"

            val notification = NotificationCompat.Builder(
                applicationContext,
                DailyQuoteNotification.CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Quote of the Day")
                .setContentText("$content — $author")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("$content\n\n— $author")
                )
                .setAutoCancel(true)
                .build()

            // If permission is missing, just exit gracefully
            if (ActivityCompat.checkSelfPermission(
                    this as Context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
            }
            NotificationManagerCompat.from(applicationContext)
                .notify(1001, notification)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
