package cz.muni.fi.pv239.drinkup.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DrinkDefinitions")
data class DrinkDefinition (
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    var name: String = "",
    var price: Double = 0.0,
    var volume: Double = 0.0,
    var abv: Double = 0.0,
    var category: Category = Category.BEER
): Parcelable {


    constructor(parcel: Parcel): this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        Category.values()[parcel.readInt()]
    )

    companion object CREATOR : Parcelable.Creator<DrinkDefinition> {
        override fun createFromParcel(parcel: Parcel): DrinkDefinition {
            return DrinkDefinition(parcel)
        }

        override fun newArray(size: Int): Array<DrinkDefinition?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        if (id != null) {
            dest?.writeLong(id)
        }
        else {
            dest?.writeLong(0)
        }
        dest?.writeString(name)
        dest?.writeDouble(price)
        dest?.writeDouble(volume)
        dest?.writeDouble(abv)
        dest?.writeInt(category.ordinal)
    }

    override fun describeContents(): Int {
        return 0
    }
}