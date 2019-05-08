package cz.muni.fi.pv239.drinkup.fragment.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.room.RxRoom
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import cz.muni.fi.pv239.drinkup.database.dao.DrinkDao
import cz.muni.fi.pv239.drinkup.database.entity.Category
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import cz.muni.fi.pv239.drinkup.enum.StatisticsTimePeriod
import cz.muni.fi.pv239.drinkup.event.listener.OnStatisticsTImePeriodChangeListener
import cz.muni.fi.pv239.drinkup.formatter.statistics.ChartValueFormatter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_price_chart.*
import java.util.*

class VolumeChartFragment(private val initialTimePeriod: StatisticsTimePeriod): BaseChartFragment(initialTimePeriod) {

    private var db: AppDatabase? = null
    private var drinkDao: DrinkDao? = null
    private var chartDataSubscription: Disposable? = null
    private var chart: BarChart? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(cz.muni.fi.pv239.drinkup.R.layout.fragment_category_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDb()
        initChart()
        loadData(initialTimePeriod)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (chartDataSubscription?.isDisposed == false)
            chartDataSubscription?.dispose()
    }

    private fun initDb() {
        val myContext = context
        if (myContext != null) {
            db = AppDatabase.getAppDatabase(myContext)
            drinkDao = db?.drinkDao()
        }
    }

    private fun initChart() {
        chart = price_chart
        chart?.setDrawValueAboveBar(true)
        chart?.description?.isEnabled = false
        chart?.setExtraOffsets(5F, 10F, 5F, 5F)
        chart?.animateY(1400, Easing.EaseInOutQuad)
        chart?.setDrawBarShadow(false)
        chart?.setDrawValueAboveBar(true)
        chart?.setPinchZoom(false)
        chart?.setDrawGridBackground(false)
        // val xAxisFormatter = DayAxisValueFormatter(chart)

        val xAxis = chart?.xAxis
        xAxis?.position = XAxis.XAxisPosition.BOTTOM
        xAxis?.setDrawGridLines(false)
        xAxis?.granularity = 1f // only intervals of 1 day
        xAxis?.labelCount = 7
        //xAxis?.valueFormatter = xAxisFormatter

    }

    override fun onTimePeriodChanged(timePeriod: StatisticsTimePeriod) {
        loadData(timePeriod)
    }

    private fun loadData(timePeriod: StatisticsTimePeriod) {
        chartDataSubscription = RxRoom.createFlowable(db)
            .observeOn(Schedulers.io())
            .map { db?.drinkDao()?.getAllDrinks() ?: error("DB Error")}
            //.map { DrinkByDateFilter.filter(it, StatisticsTimePeriod.getFromDate(timePeriod) , Date()) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                drawChart(it)
            }
    }

    private fun drawChart(drinks: List<Drink>) {
        val entries = calculateBarChartEntries(drinks)
        val dataSet = BarDataSet(entries, "Volume")
        val colors = createColors()

        val data = BarData(dataSet)
        data.setValueFormatter(ChartValueFormatter("ml"))
        data.setValueTextSize(15f)
        data.setValueTextColor(Color.WHITE)
        chart?.data = data
        dataSet.colors = colors

        chart?.highlightValues(null)
        chart?.data?.notifyDataChanged()
        chart?.notifyDataSetChanged()
        chart?.animateY(1400, Easing.EaseInOutQuad)
        chart?.invalidate()
    }

    private fun createColors(): List<Int> {
        val colors = ArrayList<Int>()
        for (c in ColorTemplate.MATERIAL_COLORS)
            colors.add(c)
        colors.add(ColorTemplate.getHoloBlue())
        return colors
    }

    private fun calculateBarChartEntries(drinks: List<Drink>): List<BarEntry> {
        val categoriesCountMap = HashMap<Category, Int>()
        val chartEntries = ArrayList<BarEntry>()

        Category.values().forEach { categoriesCountMap[it] = 0 }
        drinks.forEach {
            val categoryCount = (categoriesCountMap[it.category])
            if (categoryCount != null) categoriesCountMap[it.category] = categoryCount + 1
        }

        Category.values().forEach { chartEntries.add(
            BarEntry(
                it.ordinal.toFloat(),
                categoriesCountMap[it]?.toFloat() ?: 0.toFloat()
            )
        ) }
        return chartEntries
    }

}