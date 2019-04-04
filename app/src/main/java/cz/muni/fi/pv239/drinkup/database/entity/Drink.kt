package cz.muni.fi.pv239.drinkup.database.entity

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
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
): Parcelable {


        constructor(parcel: Parcel): this(
                parcel.readLong(),
                parcel.readString(),
                parcel.readInt(),
                parcel.readDouble(),
                parcel.readInt(),
                Category.values()[parcel.readInt()],
                parcel.readParcelable(Location::class.java.classLoader)
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
                if (price != null) dest?.writeInt(price)
                if (volume != null) dest?.writeDouble(volume)
                if (abv != null) dest?.writeInt(abv)
                if (category != null) dest?.writeInt(category.ordinal)
                if (location != null) dest?.writeParcelable(location, Parcelable.PARCELABLE_WRITE_RETURN_VALUE)
        }

        override fun describeContents(): Int {
                return 0
        }
}