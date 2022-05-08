import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager


/**
 * SuperActivity that is common to all activities using localization
 */
class LocationHelper {
    private var userLocation: Location? = null

    fun getUserLatitude(): Double?{
        return userLocation?.latitude
    }

    fun getUserLongitude(): Double?{
        return userLocation?.longitude
    }

    /**
     * Function that updates the user's current coordinates
     * @param context: the context of the activity using the coordinates
     */
    fun updateCoordinates(context: Context) {
        val futureLocation = GeneralLocationManager.get().getCurrentLocation(context)
        futureLocation.thenAccept {
            userLocation = Location(LocationManager.GPS_PROVIDER)
            userLocation?.longitude = it.longitude
            userLocation?.latitude = it.latitude
        }
    }

    /**
     * Updates handles the user's current coordinates as wanted, or returns to the
     * main activity in case of errors.
     * @param : context:
     */
    fun requireAndHandleCoordinates(context: Context, onSuccess: (location: Location) -> Unit) {
        val futureLocation = GeneralLocationManager.get().getCurrentLocation(context)
        futureLocation.handle { location, exception ->
            if (exception != null) {
                // In case the permission to access the location is missing
                val intent = Intent(context, MainPageActivity::class.java)
                context.startActivity(intent)
            } else {
                onSuccess(location)
            }
        }
    }
}