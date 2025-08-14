package com.xinhao.myapplication.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.xinhao.myapplication.MainActivity
import com.xinhao.myapplication.R
import com.xinhao.myapplication.data.model.BudgetUsage
import com.xinhao.myapplication.data.model.Transaction
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class FinanceNotificationManager(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID_DAILY_REMINDER = "daily_reminder"
        const val CHANNEL_ID_BUDGET_ALERT = "budget_alert"
        const val CHANNEL_ID_TRANSACTION = "transaction"
        
        const val NOTIFICATION_ID_DAILY_REMINDER = 1001
        const val NOTIFICATION_ID_BUDGET_ALERT = 1002
        const val NOTIFICATION_ID_TRANSACTION = 1003
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            
            // 每日记账提醒频道
            val dailyReminderChannel = NotificationChannel(
                CHANNEL_ID_DAILY_REMINDER,
                "每日记账提醒",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "提醒您每日记账"
                enableLights(true)
                enableVibration(true)
            }
            
            // 预算提醒频道
            val budgetAlertChannel = NotificationChannel(
                CHANNEL_ID_BUDGET_ALERT,
                "预算提醒",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "预算超支和接近限制提醒"
                enableLights(true)
                enableVibration(true)
            }
            
            // 交易通知频道
            val transactionChannel = NotificationChannel(
                CHANNEL_ID_TRANSACTION,
                "交易通知",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "交易相关的通知"
                enableLights(false)
                enableVibration(false)
            }
            
            notificationManager.createNotificationChannels(
                listOf(dailyReminderChannel, budgetAlertChannel, transactionChannel)
            )
        }
    }
    
    // 设置每日记账提醒
    fun scheduleDailyReminder(hour: Int, minute: Int) {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            1, TimeUnit.DAYS
        ).setInitialDelay(calculateInitialDelay(hour, minute), TimeUnit.MILLISECONDS)
            .addTag("daily_reminder")
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    // 取消每日记账提醒
    fun cancelDailyReminder() {
        WorkManager.getInstance(context).cancelAllWorkByTag("daily_reminder")
    }
    
    // 显示每日记账提醒
    fun showDailyReminder() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_DAILY_REMINDER)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("记账提醒")
            .setContentText("今天还没有记账哦，记得记录您的收支情况！")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(
            NOTIFICATION_ID_DAILY_REMINDER, notification
        )
    }
    
    // 显示预算提醒
    fun showBudgetAlert(budgetUsage: BudgetUsage) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val title = if (budgetUsage.isOverBudget) "预算超支提醒" else "预算接近限制"
        val text = if (budgetUsage.isOverBudget) {
            "${budgetUsage.category ?: "总预算"}已超支${budgetUsage.usedAmount - budgetUsage.amount}元"
        } else {
            "${budgetUsage.category ?: "总预算"}已使用${(budgetUsage.usagePercentage * 100).toInt()}%，请注意控制支出"
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_BUDGET_ALERT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(
            NOTIFICATION_ID_BUDGET_ALERT, notification
        )
    }
    
    // 显示交易成功通知
    fun showTransactionNotification(transaction: Transaction) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val title = if (transaction.type.name == "INCOME") "收入记录成功" else "支出记录成功"
        val text = "${transaction.category}: ${transaction.amount}元"
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_TRANSACTION)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(
            NOTIFICATION_ID_TRANSACTION, notification
        )
    }
    
    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val now = LocalDateTime.now()
        var targetTime = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        
        if (targetTime.isBefore(now)) {
            targetTime = targetTime.plusDays(1)
        }
        
        return Duration.between(now, targetTime).toMillis()
    }
}

// 每日记账提醒Worker
class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    override fun doWork(): Result {
        val notificationManager = FinanceNotificationManager(applicationContext)
        notificationManager.showDailyReminder()
        return Result.success()
    }
}
