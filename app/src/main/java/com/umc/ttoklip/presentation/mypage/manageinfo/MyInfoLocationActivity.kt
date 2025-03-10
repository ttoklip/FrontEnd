package com.umc.ttoklip.presentation.mypage.manageinfo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.UiSettings
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.umc.ttoklip.R
import com.umc.ttoklip.databinding.ActivityMyinfoLocationBinding
import com.umc.ttoklip.presentation.base.BaseActivity
import com.umc.ttoklip.util.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyInfoLocationActivity :
    BaseActivity<ActivityMyinfoLocationBinding>(R.layout.activity_myinfo_location),
    OnMapReadyCallback {

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var uiSetting: UiSettings
    private val LOCATION_PERMISSION_REQUEST_CODE: Int = 5000
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
//    private lateinit var range: String
//    private lateinit var circle: CircleOverlay
    private lateinit var marker: Marker

    private var locationok: Boolean = false
    private val viewModel:ManageMyInfoViewModel by viewModels()


    override fun initView() {
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE)
        }
        initMapView()
//        circle = CircleOverlay()
        marker=Marker()

//        range = getString(R.string.range_500m)
//        binding.locationRangeDescTv.text =
//            getString(R.string.range_setting_format, range)
//
//        binding.locationRange1Tv.setOnSingleClickListener { setRange500m() }
//        binding.locationRange2Tv.setOnSingleClickListener { setRange1km() }
//        binding.locationRange3Tv.setOnSingleClickListener { setRange15km() }
//
//        binding.locationRangeBar.setOnSeekBarChangeListener(object :
//            SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                if (progress <= RANGE_500M_PROGRESS) {
//                    setRange500m()
//                } else if (progress <= RANGE_1KM_PROGRESS) {
//                    setRange1km()
//                } else {
//                    setRange15km()
//                }
//                if (locationok) setcircle()
//            }
//            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                seekBar?.let {
//                    if (it.progress <= RANGE_500M_PROGRESS) {
//                        it.progress = RANGE_500M_PROGRESS
//                        setRange500m()
//                    } else if (it.progress <= RANGE_1KM_PROGRESS) {
//                        it.progress = RANGE_1KM_PROGRESS
//                        setRange1km()
//                    } else {
//                        it.progress = RANGE_15kM_PROGRESS
//                        setRange15km()
//                    }
//                }
//                if (locationok) setcircle()
//            }
//        })
//
        binding.locationBackIb.setOnSingleClickListener {
            sendResult()
            finish()
        }
    }

    override fun onBackPressed() {
        sendResult()
        super.onBackPressed()
    }

    private var locationX:Int=0
    private var locationY:Int=0
    private fun sendResult() {
        val intent=Intent()
        intent.putExtra("location",viewModel.address.value)
        intent.putExtra("locationX",locationX)
        intent.putExtra("locationY",locationY)
        setResult(Activity.RESULT_OK,intent)
    }

    override fun onMapReady(p0: NaverMap) {
        this.naverMap = p0
        uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = false
        binding.locationNowLocation.map = naverMap

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        naverMap.setOnMapClickListener { pointF, latLng ->
            setlocation(latLng.latitude,latLng.longitude)
        }
    }

    private fun setlocation(latitude:Double,longitude:Double){
        getAddress(latitude, longitude)
//        circle.center = LatLng(latitude, longitude)
        marker.position=LatLng(latitude,longitude)
        locationok = true
//        setcircle()
        marker.map=null
        marker.map=naverMap
    }

//    private fun setcircle() {
//        if (range == "500m") circle.radius = 500.0
//        else if (range == "1km") circle.radius = 1000.0
//        else circle.radius = 1500.0
//        circle.color = ContextCompat.getColor(this, R.color.yellow_trans30)
//        circle.map = naverMap
//    }

    private fun hasPermission(): Boolean {
        for (permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
    private fun initMapView() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.location_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.location_map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    private fun getAddress(latitude: Double, longitude: Double) {
        viewModel.getLegalcode(LatLng(latitude, longitude))
//        val geocoder = Geocoder(applicationContext, Locale.KOREAN)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            val addressList: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
//            if (addressList != null && addressList.isNotEmpty()) {
//                val address: Address = addressList[0]
//                val spliteAddr = address.getAddressLine(0).split(" ")
//                for(i in 1.. spliteAddr.size-1){
//                    this.address=this.address+spliteAddr[i]+" "
//                }
//            }
//        } else {
//            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
//            if (addresses != null) {
//                val spliteAddr = addresses[0].getAddressLine(0).split(" ")
//                for(i in 1.. spliteAddr.size-1){
//                    this.address=this.address+spliteAddr[i]+" "
//                }
//            }
//        }
    }

    override fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                viewModel.address.collect{
                    binding.locationMytownDetailTv.text=it
                }
            }
        }
    }

//    private fun setRange15km() {
//        range = getString(R.string.range_1_5km)
//        binding.locationRangeBar.progress = RANGE_15kM_PROGRESS
//        binding.locationRange1Tv.setTextColor(ContextCompat.getColor(this, R.color.gray40))
//        binding.locationRange2Tv.setTextColor(ContextCompat.getColor(this, R.color.gray40))
//        binding.locationRange3Tv.setTextColor(ContextCompat.getColor(this, R.color.black))
//        binding.locationRangeDescTv.text = getString(R.string.range_setting_format, range)
//    }
//
//    private fun setRange1km() {
//        range = getString(R.string.range_1km)
//        binding.locationRangeBar.progress = RANGE_1KM_PROGRESS
//        binding.locationRange1Tv.setTextColor(ContextCompat.getColor(this, R.color.gray40))
//        binding.locationRange2Tv.setTextColor(ContextCompat.getColor(this, R.color.black))
//        binding.locationRange3Tv.setTextColor(ContextCompat.getColor(this, R.color.gray40))
//        binding.locationRangeDescTv.text = getString(R.string.range_setting_format, range)
//    }
//
//    private fun setRange500m() {
//        range = getString(R.string.range_500m)
//        binding.locationRangeBar.progress = RANGE_500M_PROGRESS
//        binding.locationRange1Tv.setTextColor(ContextCompat.getColor(this, R.color.black))
//        binding.locationRange2Tv.setTextColor(ContextCompat.getColor(this, R.color.gray40))
//        binding.locationRange3Tv.setTextColor(ContextCompat.getColor(this, R.color.gray40))
//        binding.locationRangeDescTv.text = getString(R.string.range_setting_format, range)
//    }
//
//    companion object {
//        private const val RANGE_500M_PROGRESS = 33
//        private const val RANGE_1KM_PROGRESS = 67
//        private const val RANGE_15kM_PROGRESS = 100
//    }
}