package cz.muni.fi.pv239.drinkup.database.entity

data class Achievement(
    val text: String = "",
    val progress: Int = 0,
    val actual: Int = 0,
    val max: Int = 0
)