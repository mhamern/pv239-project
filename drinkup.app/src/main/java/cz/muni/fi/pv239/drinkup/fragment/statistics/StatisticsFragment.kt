package cz.muni.fi.pv239.drinkup.fragment.statistics

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.enum.StatisticsOption
import cz.muni.fi.pv239.drinkup.enum.StatisticsTimePeriod
import cz.muni.fi.pv239.drinkup.event.listener.OnStatisticsTImePeriodChangeListener
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.util.*

class StatisticsFragment : Fragment() {
    private var listener: OnStatisticsFragmentInteractionListener? = null
    private var timePeriodChangeListener: OnStatisticsTImePeriodChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStatisticsOptions(view.context)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnStatisticsFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnMyDrinksFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        timePeriodChangeListener = null
    }

    private fun initStatisticsOptions(context: Context) {
        val timePeriodSpinner: Spinner = statistics_time_period_spinner
        timePeriodSpinner.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, StatisticsTimePeriod.values().map { it.toStringLocalized(this.context) })
        timePeriodSpinner.setSelection(StatisticsTimePeriod.LAST_WEEK.ordinal)

        timePeriodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                return
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onTimePeriodChanged(StatisticsTimePeriod.values()[position])
            }
        }

        val statisticsOptionSpinner: Spinner = statistics_option_spinner
        statisticsOptionSpinner.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, StatisticsOption.values().map {it.toStringLocalized(this.context)})
        statisticsOptionSpinner.setSelection(StatisticsOption.CATEGORIES.ordinal)

        statisticsOptionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                return
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onStatisticsOptionChanged(StatisticsOption.values()[position])
            }
        }
    }

    private fun onStatisticsOptionChanged(statisticsOption: StatisticsOption) {
        when (statisticsOption) {
            StatisticsOption.PRICE -> displayPriceChart()
            StatisticsOption.CATEGORIES -> displayCategoriesChart()
            StatisticsOption.VOLUME -> displayVolumeChart()
        }
    }


    private fun onTimePeriodChanged(statisticsTimePeriod: StatisticsTimePeriod) {
        timePeriodChangeListener?.onTimePeriodChanged(statisticsTimePeriod)
    }

    private fun displayVolumeChart() {
        setChartFragment(VolumeChartFragment.newInstance(StatisticsTimePeriod.values()[statistics_time_period_spinner.selectedItemPosition]))
    }

    private fun displayCategoriesChart() {
        setChartFragment(CategoryChartFragment.newInstance(StatisticsTimePeriod.values()[statistics_time_period_spinner.selectedItemPosition]))
    }

    private fun displayPriceChart() {
        setChartFragment(PriceChartFragment.newInstance(StatisticsTimePeriod.values()[statistics_time_period_spinner.selectedItemPosition]))
    }


    private fun setChartFragment(fragment: BaseChartFragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.chart_fragment_container, fragment)
            ?.commit()
        timePeriodChangeListener = fragment
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnStatisticsFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onStatisticsFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            StatisticsFragment()
    }
}
