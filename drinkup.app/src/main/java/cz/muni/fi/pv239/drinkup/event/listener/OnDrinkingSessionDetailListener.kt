package cz.muni.fi.pv239.drinkup.event.listener

import android.content.Intent

interface OnDrinkingSessionDetailListener {
    fun onClickOnSession(editIntent: Intent)
}