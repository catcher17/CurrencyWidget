import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.catcher.currencywidget.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData


class CustomCurrencyView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private var flagImageView: ImageView
    private var chart: LineChart

    init {
        LayoutInflater.from(context).inflate(R.layout.currency_chart_recycler_item, this, true)
        flagImageView = findViewById(R.id.flagImageView)
        chart = findViewById(R.id.chart)
    }

    fun setCurrencyFlag(flag: Drawable) {
        flagImageView.setImageDrawable(flag)
    }

    fun setCurrencyChartData(chartData: LineData) {
        chart.data = chartData
        chart.invalidate()
    }
}