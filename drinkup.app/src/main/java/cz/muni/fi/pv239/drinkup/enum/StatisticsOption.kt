package cz.muni.fi.pv239.drinkup.enum

import android.content.Context
import cz.muni.fi.pv239.drinkup.R

// Custom value, jako u predchoziho enumu
enum class StatisticsOption {
    PRICE {
        override fun toStringLocalized(context: Context?): String {
            return context?.getString(R.string.statistics_option_price) ?: "Price"
        }
    },
    VOLUME {
        override fun toStringLocalized(context: Context?): String {
            return context?.getString(R.string.statistics_option_volume) ?: "Volume"
        }
    },
    CATEGORIES {
        override fun toStringLocalized(context: Context?): String {
            return context?.getString(R.string.statistics_option_categories) ?: "Categories"
        }
    };

    abstract fun toStringLocalized(context: Context?): String
}