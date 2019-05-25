package cz.muni.fi.pv239.drinkup.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.database.entity.Category

class AchievementService {
    companion object {
        @JvmStatic
        fun achievements(category: Category, context: Context) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            if (category == Category.BEER) {
                var actual: Int = sharedPreferences.getInt("ach_drinkBeer", 0)
                editor.putInt("ach_drinkBeer", actual+1)
            } else if (category == Category.SPIRIT) {
                var actual: Int = sharedPreferences.getInt("ach_drinkShot", 0)
                editor.putInt("ach_drinkShot", actual+1)
            }
            editor.commit()
            makeNotification(category, context)
        }

        private fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (manager.getNotificationChannel("achievement") != null) {
                    return
                }

                val name = "achievement"
                val description = "achievement channel"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("achievement", name, importance)
                channel.description = description
                manager.createNotificationChannel(channel)
            }
        }

        private fun makeNotification(category: Category, context: Context) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            var beer = sharedPreferences.getInt("ach_drinkBeer", 0)
            var shots = sharedPreferences.getInt("ach_drinkShot", 0)
            var content = ""
            var completed = false
            if (category == Category.BEER) {
                if (beer == 5) {
                    content = context.getString(R.string.ach_drink5beer)
                    completed = true

                }
                if (beer == 20) {
                    content = context.getString(R.string.ach_drink20beer)
                    completed = true
                }
                if (beer == 100) {
                    content = context.getString(R.string.ach_drink100beer)
                    completed = true
                }
            }
            if (category == Category.SPIRIT) {
                if (shots == 5) {
                    content = context.getString(R.string.ach_drink5shots)
                    completed = true
                }
                if (shots == 20) {
                    content = context.getString(R.string.ach_drink20shots)
                    completed = true
                }
            }
            if (completed) {
                createNotificationChannel(context)
                val builder = NotificationCompat.Builder(context, "achievement")
                    .setSmallIcon(R.drawable.abc_ic_star_black_16dp)
                    .setContentTitle(context.getString(R.string.achievement_completed))
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                val manager = NotificationManagerCompat.from(context)
                manager.notify(1, builder.build())
            }
        }
    }
}