package cz.muni.fi.pv239.drinkupwearable

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class MainActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setAmbientEnabled()
    }
}
