package cz.muni.fi.pv239.drinkup.event.listener

import android.content.Intent

interface EditDrinkDefinitionListener {
    fun onEditRequested(editIntent: Intent)
}