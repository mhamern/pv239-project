package cz.muni.fi.pv239.drinkup.service

enum class AddDrinkOperationResult {
    FAILED {
        override fun toString(): String {
            return "failure"
        }
    },
    SUCCEEDED {
        override fun toString(): String {
            return "success"
        }
    },
    SUCCEEDED_WITHOUT_PERMISSION {
        override fun toString(): String {
            return "success_no_permission"
        }
    };

    abstract override fun toString(): String
}
