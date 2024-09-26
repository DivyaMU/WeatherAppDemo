package com.example.WeatherAppDemo

import android.location.Location
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import com.example.WeatherAppDemo.Activity.MainActivity
import com.example.WeatherAppDemo.ViewModel.WeatherViewModel
import com.example.WeatherAppDemo.model.CurrentResponseApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class MainActivityTest {

    private lateinit var mainActivity: MainActivity

    // Mock the WeatherViewModel
    private val weatherViewModel: WeatherViewModel = mock(WeatherViewModel::class.java)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Use Robolectric to create the activity
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            mainActivity = activity
            // Inject the mocked ViewModel
            //mainActivity.weatherViewModel = weatherViewModel
        }
    }

    @Test
    fun testFetchCurrentWeatherSuccess() {
        // Mock weather data
//        val mockResponse = CurrentResponseApi(
//            weather = listOf(CurrentResponseApi.Weather("Clear", "01d")),
//            main = CurrentResponseApi.Main(25.0, 28.0, 22.0, 70),
//            wind = CurrentResponseApi.Wind(5.0)
//        )

        // Mock the ViewModel's live data
        val liveData = MutableLiveData<CurrentResponseApi>()
        //liveData.postValue(mockResponse)

        // Simulate ViewModel method call
//        `when`(weatherViewModel.loadCurrentWeather(anyDouble(), anyDouble(), anyString()))
//            .thenReturn(liveData)

        // Trigger the weather data fetching
        //mainActivity.updateWeatherBasedOnLocation(40.73, -73.93)

        // Check if the UI is updated correctly
        val statusTxt: TextView = mainActivity.findViewById(R.id.statusTxt)
        val currentTempTxt: TextView = mainActivity.findViewById(R.id.currentTempTxt)
        val windTxt: TextView = mainActivity.findViewById(R.id.windTxt)

        assertEquals("Clear", statusTxt.text.toString())
        assertEquals("25°", currentTempTxt.text.toString())
        assertEquals("5Km", windTxt.text.toString())
    }

    @Test
    fun testLocationPermissionGranted() {
        // Mock location data
        val mockLocation = mock(Location::class.java).apply {
            `when`(latitude).thenReturn(40.73)
            `when`(longitude).thenReturn(-73.93)
        }

        // Simulate the location being fetched
//        `when`(mainActivity.fusedLocationClient.lastLocation)
//            .thenReturn(mockLocation)

        // Call the method to request current location
        //mainActivity.requestCurrentLocation()

        // Verify that the ViewModel's loadCurrentWeather method is called with the correct parameters
        verify(weatherViewModel).loadCurrentWeather(40.73, -73.93, "metric")
    }

    @Test
    fun testOnFailureLoadWeather() {
        // Simulate a failed weather response
        val liveData = MutableLiveData<CurrentResponseApi>()
        liveData.postValue(null) // Simulate failure

        // Simulate ViewModel method call
//        `when`(weatherViewModel.loadCurrentWeather(anyDouble(), anyDouble(), anyString()))
//            .thenReturn(liveData)

        // Trigger the weather data fetching
       // mainActivity.updateWeatherBasedOnLocation(40.73, -73.93)

        // Verify that the UI is updated correctly for failure case
        val detailLayout: View = mainActivity.findViewById(R.id.detailLayout)
        val progressBar: View = mainActivity.findViewById(R.id.progressBar)

        assertEquals(View.GONE, detailLayout.visibility)
        assertEquals(View.GONE, progressBar.visibility)
    }
}
