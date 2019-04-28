package cz.muni.fi.pv239.drinkup.fragment

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
import kotlinx.android.synthetic.main.activity_edit_drink.*
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.util.*

class StatisticsFragment : Fragment() {
    private var listener: OnStatisticsFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStatisticsOptions(view.context)
    }

    private fun initStatisticsOptions(context: Context) {
        val timePeriodSpinner: Spinner = statistics_time_period_spinner
        timePeriodSpinner.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, StatisticsTimePeriod.values())
        timePeriodSpinner.setSelection(StatisticsTimePeriod.LAST_WEAK.ordinal)

        timePeriodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                return
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onTimePeriodChanged(StatisticsTimePeriod.values()[position])
            }
        }

        val statisticsOptionSpinner: Spinner = statistics_option_spinner
        statisticsOptionSpinner.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, StatisticsOption.values())
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val date = Date()

        if (context is OnStatisticsFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnMyDrinksFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    private fun onStatisticsOptionChanged(statisticsOption: StatisticsOption) {
        when (statisticsOption) {
            StatisticsOption.PRICE -> return
            StatisticsOption.CATEGORIES -> return
            StatisticsOption.VOLUME -> return
        }
    }

    private fun onTimePeriodChanged(statisticsTimePeriod: StatisticsTimePeriod) {
        when (statisticsTimePeriod) {
            StatisticsTimePeriod.LAST_WEAK -> return
            StatisticsTimePeriod.LAST_MONTH -> return
            StatisticsTimePeriod.LAST_YEAR -> return
            StatisticsTimePeriod.ALL_TIME -> return
        }
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StatisticsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            StatisticsFragment()
    }
}
