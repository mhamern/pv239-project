package cz.muni.fi.pv239.drinkup.enum

enum class StatisticsOption {
    PRICE {
        override fun toString(): String {
            return "Price"
        }
    },
    VOLUME {
        override fun toString(): String {
            return "Volume"
        }
    },
    CATEGORIES {
        override fun toString(): String {
            return "Categories"
        }
    };

    abstract override fun toString(): String;
}