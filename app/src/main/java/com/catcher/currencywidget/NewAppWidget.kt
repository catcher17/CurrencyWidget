package com.catcher.currencywidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {
    //TODO:переделать на gridview
    companion object {
        const val WIDGET_CURRENCY_FROM_NUMBER = "widget_currency_from_number"
        const val WIDGET_SHARED_PREFERENCES = "widget_shared_preferences"

    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        val editor: SharedPreferences.Editor = context!!.getSharedPreferences(
            WIDGET_SHARED_PREFERENCES, Context.MODE_PRIVATE
        ).edit()
        for (widgetID in appWidgetIds!!) {
            editor.remove(WIDGET_CURRENCY_FROM_NUMBER + widgetID)
        }
        editor.apply()
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val buttonNumbersIDs = arrayListOf<Int>(R.id.widgetImageButton0,R.id.widgetImageButton1,
            R.id.widgetImageButton2,R.id.widgetImageButton3,
            R.id.widgetImageButton4,R.id.widgetImageButton5,
            R.id.widgetImageButton6,R.id.widgetImageButton7,
            R.id.widgetImageButton8,R.id.widgetImageButton9,)
        lateinit var pendingIntent: PendingIntent
        val views = RemoteViews(context.packageName, R.layout.new_app_widget)
        val sharedPreferences = context.getSharedPreferences(
            WIDGET_SHARED_PREFERENCES, Context.MODE_PRIVATE
        )

        val widgetCurrencyNumberFrom = sharedPreferences.getInt(WIDGET_CURRENCY_FROM_NUMBER + appWidgetId, 0)
        views.setTextViewText(R.id.widgetFromCurrencyTextView, widgetCurrencyNumberFrom.toString())
        for (i in 0..9) {
            val clickOnNumberIntent = Intent(context, NewAppWidget::class.java)
            clickOnNumberIntent.action = "com.catcher.currencywidget.action.NUMBER_CLICKED"
            clickOnNumberIntent.putExtra("widgetId", appWidgetId)
            clickOnNumberIntent.putExtra("pressedNumber", i)
            clickOnNumberIntent.data = Uri.parse("intent$i")
            pendingIntent = PendingIntent.getBroadcast(
                context,
                appWidgetId,
                clickOnNumberIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(buttonNumbersIDs[i], pendingIntent)
        }

        val clickOnDeleteIntent = Intent(context, NewAppWidget::class.java)
        clickOnDeleteIntent.action = "com.catcher.currencywidget.action.DELETE_CLICKED"
        clickOnDeleteIntent.putExtra("widgetId", appWidgetId)
        pendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            clickOnDeleteIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetDeleteButton, pendingIntent)

        val test = Intent(context, WidgetSettingsActivity::class.java)
        pendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            test,
            PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetSettingsButton, pendingIntent)


        appWidgetManager.updateAppWidget(appWidgetId, views)

    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val appWidgetId = intent.getIntExtra(
            "widgetId",
            0
        )
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val views = RemoteViews(context.packageName, R.layout.new_app_widget)
        val sharedPreferences = context.getSharedPreferences(
            WIDGET_SHARED_PREFERENCES, Context.MODE_PRIVATE
        )
        if (intent.action.equals("com.catcher.currencywidget.action.NUMBER_CLICKED")) {
            val pressedDigit = intent.getIntExtra("pressedNumber",0)
            val widgetCurrencyNumberFrom = sharedPreferences.getInt(WIDGET_CURRENCY_FROM_NUMBER + appWidgetId, 0)
            val newNumber =  (widgetCurrencyNumberFrom.toString()+pressedDigit.toString()).toInt()

            views.setTextViewText(R.id.widgetFromCurrencyTextView, newNumber.toString())
            sharedPreferences.edit().putInt(
                WIDGET_CURRENCY_FROM_NUMBER + appWidgetId,
                newNumber
            ).apply()
        }
        if(intent.action.equals("com.catcher.currencywidget.action.DELETE_CLICKED")) {
            val widgetCurrencyNumberFrom = sharedPreferences.getInt(WIDGET_CURRENCY_FROM_NUMBER + appWidgetId, 0)
            if(widgetCurrencyNumberFrom!=0) {
                val newNumber = widgetCurrencyNumberFrom/10
                views.setTextViewText(R.id.widgetFromCurrencyTextView, newNumber.toString())
                sharedPreferences.edit().putInt(
                    WIDGET_CURRENCY_FROM_NUMBER + appWidgetId,
                    newNumber
                ).apply()
            }

        }
        appWidgetManager.updateAppWidget(appWidgetId, views)

    }
}
