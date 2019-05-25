package cz.muni.fi.pv239.drinkup.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.RxRoom
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import cz.muni.fi.pv239.drinkup.utils.MapItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.settings.*
import java.util.*

class MapActivity: AppCompatActivity(), OnMapReadyCallback {

    private var db: AppDatabase? = null
    private var getDrinksSubscription: Disposable? = null
    private var clusterManager: ClusterManager<MapItem>? = null
    private var items: MutableList<MapItem> = mutableListOf()

    override fun onMapReady(p0: GoogleMap?) {
        var sessionId = intent.getLongExtra("sessionid", 0)
        db = AppDatabase.getAppDatabase(this)
        clusterManager = ClusterManager<MapItem>(this, p0)
        p0?.setOnCameraIdleListener(clusterManager)
        p0?.setOnMarkerClickListener(clusterManager)
        getDrinksSubscription = RxRoom.createFlowable(db)
            .observeOn(Schedulers.io())
            .map{db?.sessionDao()?.getAllDrinks(sessionId) ?: Collections.emptyList()}
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                setMarkers(it, p0)
            }
    }

    private fun setMarkers(drinks: List<Drink>, googleMap: GoogleMap?) {
        for(drink in drinks) {
            var latlng: LatLng
            if (drink.latitude != 0.0 && drink.longitude != 0.0) {
                latlng = LatLng(drink.latitude, drink.longitude)
                items.add(MapItem(latlng))
                var cameraMove = LatLng(drink.latitude, drink.longitude)
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraMove, 14F))
            }
        }
        clusterManager?.addItems(items)
        clusterManager?.cluster()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setupActionBar()
        var options = GoogleMapOptions()
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
        options.zoomControlsEnabled(true)
        options.compassEnabled(true)
        var mapFragment = SupportMapFragment.newInstance(options)
        var ft = supportFragmentManager.beginTransaction().replace(R.id.map, mapFragment)
        ft.commit()
        mapFragment.getMapAsync(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(map_toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}