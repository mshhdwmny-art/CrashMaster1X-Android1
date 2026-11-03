package com.crashmaster.one

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class OverlayService : Service() {
    private lateinit var manager: WindowManager
    private var panel: LinearLayout? = null
    private lateinit var stats: TextView
    private val multipliers = mutableListOf<Double>()

    override fun onCreate() {
        super.onCreate()
        manager = getSystemService(WINDOW_SERVICE) as WindowManager
        showOverlay()
    }

    private fun refreshStats() {
        val avg = if (multipliers.isEmpty()) "--" else "%.2fx".format(multipliers.average())
        val last = multipliers.lastOrNull()?.let { "%.2fx".format(it) } ?: "--"
        stats.text = "الجولات المسجلة: ${multipliers.size}\nالمتوسط: $avg\nآخر نتيجة: $last"
    }

    private fun showOverlay() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(20, 16, 20, 16)
            setBackgroundColor(Color.rgb(27, 32, 61))
        }
        root.addView(TextView(this).apply {
            text = "CRASH MASTER 1X"; textSize = 17f; setTextColor(Color.WHITE)
        })
        val field = EditText(this).apply {
            hint = "أدخل المضاعف مثل 2.13"; setSingleLine(true)
            setTextColor(Color.WHITE); setHintTextColor(Color.LTGRAY)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        root.addView(field)
        root.addView(Button(this).apply {
            text = "تسجيل النتيجة"
            setOnClickListener {
                val value = field.text.toString().replace(",", ".").toDoubleOrNull()
                if (value != null && value >= 1.0) {
                    multipliers.add(value); refreshStats(); field.text.clear()
                } else field.error = "أدخل مضاعفاً صحيحاً أكبر من أو يساوي 1"
            }
        })
        stats = TextView(this).apply { textSize = 14f; setTextColor(Color.WHITE); setPadding(0, 10, 0, 10) }
        root.addView(stats); refreshStats()
        root.addView(Button(this).apply { text = "إغلاق النافذة"; setOnClickListener { stopSelf() } })
        val windowType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            windowType, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.END; x = 16; y = 160 }
        panel = root
        manager.addView(root, params)
    }

    override fun onDestroy() {
        panel?.let { try { manager.removeView(it) } catch (_: Exception) {} }
        panel = null
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
