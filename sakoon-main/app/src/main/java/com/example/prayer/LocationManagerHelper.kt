package com.example.prayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import java.util.Locale

data class CityLocation(
    val nameBn: String,
    val nameEn: String,
    val latitude: Double,
    val longitude: Double
)

class LocationManagerHelper(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sakoon_prayer_loc", Context.MODE_PRIVATE)

    companion object {
        val DEFAULT_CITIES = listOf(
            CityLocation("ঢাকা", "Dhaka", 23.8103, 90.4125),
            CityLocation("চট্টগ্রাম", "Chattogram", 22.3569, 91.7832),
            CityLocation("সিলেট", "Sylhet", 24.8949, 91.8687),
            CityLocation("রাজশাহী", "Rajshahi", 24.3636, 88.6241),
            CityLocation("খুলনা", "Khulna", 22.8456, 89.5403),
            CityLocation("বরিশাল", "Barishal", 22.7010, 90.3535),
            CityLocation("রংপুর", "Rangpur", 25.7439, 89.2752),
            CityLocation("ময়মনসিংহ", "Mymensingh", 24.7471, 90.4203),
            CityLocation("কুমিল্লা", "Cumilla", 23.4682, 91.1788),
            CityLocation("মক্কা মুকাররমা", "Makkah", 21.4225, 39.8262),
            CityLocation("মদিনা মুনাওয়ারা", "Madinah", 24.4672, 39.6111)
        )
    }

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun getSavedLocation(): CityLocation {
        val name = prefs.getString("loc_name", "ঢাকা") ?: "ঢাকা"
        val lat = prefs.getFloat("loc_lat", 23.8103f).toDouble()
        val lng = prefs.getFloat("loc_lng", 90.4125f).toDouble()
        return CityLocation(name, name, lat, lng)
    }

    fun saveLocation(city: CityLocation) {
        prefs.edit()
            .putString("loc_name", city.nameBn)
            .putFloat("loc_lat", city.latitude.toFloat())
            .putFloat("loc_lng", city.longitude.toFloat())
            .apply()
    }

    fun getSavedJuristicMethod(): JuristicMethod {
        val name = prefs.getString("juristic_method", JuristicMethod.HANAFI.name)
        return try {
            JuristicMethod.valueOf(name ?: JuristicMethod.HANAFI.name)
        } catch (_: Exception) {
            JuristicMethod.HANAFI
        }
    }

    fun saveJuristicMethod(method: JuristicMethod) {
        prefs.edit().putString("juristic_method", method.name).apply()
    }

    @SuppressLint("MissingPermission")
    fun requestCurrentGpsLocation(onLocationFound: (CityLocation) -> Unit) {
        if (!hasLocationPermission()) {
            onLocationFound(getSavedLocation())
            return
        }

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (lm == null) {
            onLocationFound(getSavedLocation())
            return
        }

        // Try last known location first (fast)
        val lastGps = try { lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) } catch (_: Exception) { null }
        val lastNet = try { lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } catch (_: Exception) { null }
        val bestLast = lastGps ?: lastNet

        if (bestLast != null) {
            val city = resolveCityName(bestLast.latitude, bestLast.longitude)
            saveLocation(city)
            onLocationFound(city)
            return
        }

        // If no last known, request a single update
        try {
            val listener = object : LocationListener {
                override fun onLocationChanged(loc: Location) {
                    val city = resolveCityName(loc.latitude, loc.longitude)
                    saveLocation(city)
                    onLocationFound(city)
                    lm.removeUpdates(this)
                }
                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null)
            } else if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null)
            } else {
                onLocationFound(getSavedLocation())
            }
        } catch (e: Exception) {
            onLocationFound(getSavedLocation())
        }
    }

    private fun resolveCityName(lat: Double, lng: Double): CityLocation {
        // Try reverse Geocoding if available
        try {
            val geocoder = Geocoder(context, Locale("bn"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val city = addresses[0].locality ?: addresses[0].subAdminArea ?: addresses[0].adminArea
                    if (!city.isNullOrBlank()) {
                        return CityLocation(city, city, lat, lng)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val city = addresses[0].locality ?: addresses[0].subAdminArea ?: addresses[0].adminArea
                    if (!city.isNullOrBlank()) {
                        return CityLocation(city, city, lat, lng)
                    }
                }
            }
        } catch (_: Exception) {
            // Geocoder service not available, fallback to closest city or "বর্তমান অবস্থান"
        }

        // Find closest from known cities
        var closest = DEFAULT_CITIES.first()
        var minDistance = Double.MAX_VALUE
        for (c in DEFAULT_CITIES) {
            val d = Math.hypot(c.latitude - lat, c.longitude - lng)
            if (d < minDistance) {
                minDistance = d
                closest = c
            }
        }

        return if (minDistance < 0.5) {
            CityLocation("${closest.nameBn} (GPS)", closest.nameEn, lat, lng)
        } else {
            CityLocation("বর্তমান অবস্থান (GPS)", "GPS Location", lat, lng)
        }
    }
}
