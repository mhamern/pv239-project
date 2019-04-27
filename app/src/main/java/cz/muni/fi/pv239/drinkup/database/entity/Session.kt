package cz.muni.fi.pv239.drinkup.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Sessions")
data class Session(
        @PrimaryKey(autoGenerate = true) val id: Long? = null,
        var title: String = "",
        var drinks: List<Drink> = listOf()
) {
}