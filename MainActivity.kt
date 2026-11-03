package com.crashmaster.one

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 36, 28, 36)
            setBackgroundColor(Color.rgb(16, 19, 41))
        }
        page.addView(TextView(this).apply {
            text = "CRASH MASTER 1X"; textSize = 26f
            setTextColor(Color.WHITE); gravity = Gravity.CENTER
        })
        page.addView(TextView(this).apply {
            text = "مساعد إحصائي لمتابعة المضاعفات السابقة"
            textSize = 16f; setTextColor(Color.LTGRAY); gravity = Gravity.CENTER
            setPadding(0, 18, 0, 28)
        })
        page.addView(Button(this).apply {
            text = "السماح بالظهور فوق التطبيقات"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                } else startService(Intent(this@MainActivity, OverlayService::class.java))
            }
        })
        page.addView(Button(this).apply {
            text = "تشغيل النافذة العائمة"
            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) startService(Intent(this@MainActivity, OverlayService::class.java))
                else startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            }
        })
        page.addView(TextView(this).apply {
            text = "الإصدار الأول: نافذة عائمة وسجل يدوي. قراءة الشاشة التلقائية ستضاف بعد اختبار النسخة الأساسية."
            textSize = 14f; setTextColor(Color.LTGRAY); gravity = Gravity.CENTER
            setPadding(0, 28, 0, 0)
        })
        setContentView(page)
    }
}
