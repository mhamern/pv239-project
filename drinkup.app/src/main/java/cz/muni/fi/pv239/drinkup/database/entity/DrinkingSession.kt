package cz.muni.fi.pv239.drinkup.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "Sessions")
data class DrinkingSession(
        @PrimaryKey(autoGenerate = true) val id: Long? = null,
        var title: String = "",
        var created: Date = Date()
): Parcelable {

    constructor(parcel: Parcel): this(
            parcel.readLong(),
            parcel.readString() ?: "",
            Date(parcel.readLong())
    )

    companion object CREATOR: Parcelable.Creator<DrinkingSession>{
        override fun newArray(size: Int): Array<DrinkingSession?> {
            return arrayOfNulls(size)
        }

        override fun createFromParcel(source: Parcel): DrinkingSession {
           return DrinkingSession(source)
        }

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        if (id != null) dest?.writeLong(id)
        dest?.writeString(title)
        dest?.writeLong(created.time)
    }

}