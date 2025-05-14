package com.example.products.framework.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.products.R
import com.example.products.framework.di.ProductRepositoryEntryPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// annotation tells Hilt that this is a worker class where dependencies should be injected using Hilt
// CoroutineWorker - abstract class that lets you run background work using coroutines

@HiltWorker
class ProductSyncWorker @AssistedInject constructor( // mix Hilt dependencies + manually passed args
    @Assisted appContext: Context,                  // passed manually by WorkManager
    @Assisted workerParams: WorkerParameters        // passed manually by WorkManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("ProductSyncWorker", "Sync started at ${System.currentTimeMillis()}")

        showRunningNotification()

        return@withContext try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                ProductRepositoryEntryPoint::class.java
            )

            val repository = entryPoint.productRepository()

            repository.syncProducts()

            Log.d("ProductSyncWorker", "Product inserted from background work")
            Result.success()
        } catch (e: Exception) {
            Log.e("ProductSyncWorker", "Work failed: ${e.message}", e)
            Result.retry()
        }
    }

    private fun showRunningNotification() {
        val channelId = "product_sync_channel"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Product Sync",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Products App")
            .setContentText("Product sync is running...")
            .setSmallIcon(R.drawable.ic_sync)
            .build()

        notificationManager.notify(1, notification)
    }
}
