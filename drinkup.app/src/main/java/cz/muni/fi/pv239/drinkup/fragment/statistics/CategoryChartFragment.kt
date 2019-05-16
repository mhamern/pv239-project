package cz.muni.fi.pv239.drinkup.fragment.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.room.RxRoom
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import cz.muni.fi.pv239.drinkup.database.dao.DrinkDao
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import cz.muni.fi.pv239.drinkup.enum.StatisticsTimePeriod
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import cz.muni.fi.pv239.drinkup.database.entity.Category
import android.graphics.Color
import android.location.Location
import android.location.LocationProvider
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.data.PieData
import kotlinx.android.synthetic.main.fragment_category_chart.*
import com.github.mikephil.charting.animation.Easing
import khronos.*


class CategoryChartFragment(private val initialTimePeriod: StatisticsTimePeriod): BaseChartFragment(initialTimePeriod) {
    private var db: AppDatabase? = null
    private var drinkDao: DrinkDao? = null
    private var chartDataSubscription: Disposable? = null
    private var chart: PieChart? = null


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
        chart = category_pie_chart
        chart?.setUsePercentValues(true)
        chart?.description?.isEnabled = false
        chart?.setExtraOffsets(5F, 10F, 5F, 5F)
        chart?.setEntryLabelColor(Color.WHITE)
        chart?.setEntryLabelTextSize(12f)
    }

    override fun onTimePeriodChanged(timePeriod: StatisticsTimePeriod) {
        loadData(timePeriod)
    }

    private fun loadData(timePeriod: StatisticsTimePeriod) {
        val fromDate = StatisticsTimePeriod.getFromDate(timePeriod)
        val toDate = Date()
        chartDataSubscription = RxRoom.createFlowable(db)
            .observeOn(Schedulers.io())
            .map { db?.drinkDao()?.getDrinksFromToDate(fromDate, toDate) ?: error("DB Error")}
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                drawChart(it)
            }
    }

    private fun drawChart(drinks: List<Drink>) {
        val entries = calculatePieChartEntries(drinks)
        val dataSet = PieDataSet(entries, "Categories")

        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        val colors = createColors()

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(chart))
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

    private fun calculatePieChartEntries(drinks: List<Drink>): List<PieEntry> {
        val categoriesCountMap = HashMap<Category, Int>()
        val chartEntries = ArrayList<PieEntry>()

        Category.values().forEach { categoriesCountMap[it] = 0 }
        drinks.forEach {
            val categoryCount = (categoriesCountMap[it.category])
            if (categoryCount != null) categoriesCountMap[it.category] = categoryCount + 1
        }

        Category.values().forEach { chartEntries.add(
            PieEntry(
            categoriesCountMap[it]?.toFloat() ?: 0.toFloat(),
            it.toString()
            )
        ) }
        return chartEntries
    }
}