package com.example.products.framework.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.products.framework.di.ProductRepositoryEntryPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// annotation tells Hilt that this is a worker class where dependencies should be injected using Hilt
// CoroutineWorker - abstract class that lets you run background work using coroutines

@HiltWorker
class SyncProductsWorker @AssistedInject constructor( // mix Hilt dependencies + manually passed args
    @Assisted appContext: Context,                  // passed manually by WorkManager
    @Assisted workerParams: WorkerParameters        // passed manually by WorkManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d("SyncProductsWorker", "Running background work...")

            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                ProductRepositoryEntryPoint::class.java
            )

            val repository = entryPoint.productRepository()

            repository.syncProducts()

            Log.d("SyncProductsWorker", "Product inserted from background work")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncProductsWorker", "Work failed: ${e.message}", e)
            Result.retry()
        }
    }
}
