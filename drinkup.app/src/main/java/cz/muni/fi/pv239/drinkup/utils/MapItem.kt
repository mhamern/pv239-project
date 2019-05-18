package cz.muni.fi.pv239.drinkup.utils

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MapItem(mPosition: LatLng): ClusterItem {
    private var position = mPosition
    override fun getSnippet(): String {
        return ""
    }

    override fun getTitle(): String {
        return ""
    }

    override fun getPosition(): LatLng {
        return position
    }
}