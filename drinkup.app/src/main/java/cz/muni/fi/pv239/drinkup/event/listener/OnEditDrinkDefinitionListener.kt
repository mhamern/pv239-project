package cz.muni.fi.pv239.drinkup.event.listener

import android.content.Intent

interface OnEditDrinkDefinitionListener {
    fun onEditRequested(editIntent: Intent)
}