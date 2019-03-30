package cz.muni.fi.pv239.drinkup.database.entity

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Drinks")
data class Drink(
        @PrimaryKey(autoGenerate = true) val id: Long? = null,
        val name: String? = "",
        val price: Int? = 0,
        val volume: Double? = 0.0,
        val abv: Int? = 0,
        val category: Category? = null,
        val location: Location? = null
) {
}