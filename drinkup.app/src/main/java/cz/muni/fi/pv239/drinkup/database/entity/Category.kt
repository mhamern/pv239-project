package cz.muni.fi.pv239.drinkup.database.entity

// Pokud se uklada do DB, enum vzdy s vlastnimi hodnotami! Pokud bych ted doprostred pridal prvek, rozhodi mi to historii -> nevyhoda cisteho enumu
enum class Category {
    BEER, SPIRIT, WINE, COCKTAIL
}