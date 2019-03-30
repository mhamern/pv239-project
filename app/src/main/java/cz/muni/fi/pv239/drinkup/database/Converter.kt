package cz.muni.fi.pv239.drinkup.database

import android.location.Location
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cz.muni.fi.pv239.drinkup.database.entity.Category
import cz.muni.fi.pv239.drinkup.database.entity.Drink

import java.lang.reflect.Type

class Converter {
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
        if (location == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<Location>() {}.type
        return gson.toJson(location, type)
    }

    @TypeConverter
    fun toLocation(location: String?): Location? {
        if (location == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<Location>() {}.type
        return gson.fromJson<Location>(location, type)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun <T : Enum<T>> T.toInt(): Int = this.ordinal
    private inline fun <reified T : Enum<T>> Int.toEnum(): T = enumValues<T>()[this]

    @TypeConverter fun categoryToInt(category: Category?) = category?.toInt()
    @TypeConverter fun intToCategory(category: Int?) = category?.toEnum<Category>()
}
