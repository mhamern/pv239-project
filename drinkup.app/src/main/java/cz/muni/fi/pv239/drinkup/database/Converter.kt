package cz.muni.fi.pv239.drinkup.database

import android.location.Location
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cz.muni.fi.pv239.drinkup.database.entity.Category
import cz.muni.fi.pv239.drinkup.database.entity.Drink

import java.util.*

class Converter {
    @TypeConverter
    fun toDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun toLong(value: Date?): Long? {
        return value?.time
    }

    @TypeConverter
    fun fromDrinksList(drinks: List<Drink>?): String? {
        if (drinks == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<Drink>>() {}.type
        return gson.toJson(drinks, type)
    }

    @TypeConverter
    fun toDrinkList(drinkString: String?): List<Drink>? {
        if (drinkString == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<Drink>>() {}.type
        return gson.fromJson<List<Drink>>(drinkString, type)
    }

    @TypeConverter
    fun fromLocation(location: Location?): String? {
        return if (location == null) {
            null
        } else String.format(
            Locale.US, "%f,%f", location.latitude,
            location.longitude
        )

    }

    @TypeConverter
    fun toLocation(latlon: String?): Location? {
        if (latlon == null) {
            return null
        }

        val pieces = latlon.split(",")
        val result = Location("")

        result.latitude = pieces[0].toDouble()
        result.longitude = pieces[1].toDouble()

        return result
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun <T : Enum<T>> T.toInt(): Int = this.ordinal
    private inline fun <reified T : Enum<T>> Int.toEnum(): T = enumValues<T>()[this]

    @TypeConverter fun categoryToInt(category: Category?) = category?.toInt()
    @TypeConverter fun intToCategory(category: Int?) = category?.toEnum<Category>()
}
