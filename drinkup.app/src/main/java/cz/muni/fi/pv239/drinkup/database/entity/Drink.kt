package cz.muni.fi.pv239.drinkup.database.entity

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Drinks")
data class Drink(
        @PrimaryKey(autoGenerate = true) val id: Long? = null,
        val sessionId: Long? = null,
        var name: String = "",
        var price: Double = 0.0,
        var volume: Double = 0.0,
        var abv: Double = 0.0,
        var category: Category = Category.BEER,
        val longitude: Double? = null,
        val latitude: Double? = null,
        var date: Date = Date()
): Parcelable {


        constructor(parcel: Parcel): this(
                parcel.readLong(),
                parcel.readLong(),
                parcel.readString() ?: "",
                parcel.readDouble(),
                parcel.readDouble(),
                parcel.readDouble(),
                Category.values()[parcel.readInt()],
                parcel.readDouble(),
                parcel.readDouble(),
                Date(parcel.readLong())
        )

        companion object CREATOR : Parcelable.Creator<Drink> {
                override fun createFromParcel(parcel: Parcel): Drink {
                        return Drink(parcel)
                }

                override fun newArray(size: Int): Array<Drink?> {
                        return arrayOfNulls(size)
                }
        }

        override fun writeToParcel(dest: Parcel?, flags: Int) {
                dest?.writeString(name)
                if (id != null) dest?.writeLong(id)
                if (sessionId != null) dest?.writeLong(sessionId)
                dest?.writeDouble(price)
                dest?.writeDouble(volume)
                dest?.writeDouble(abv)
                dest?.writeInt(category.ordinal)
                if (longitude != null) dest?.writeDouble(longitude)
                if (latitude != null) dest?.writeDouble(latitude)
                dest?.writeLong(date.time)
        }

        override fun describeContents(): Int {
                return 0
        }
}