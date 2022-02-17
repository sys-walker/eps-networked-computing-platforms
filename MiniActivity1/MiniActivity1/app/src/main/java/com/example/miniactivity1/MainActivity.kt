package com.example.miniactivity1

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.lights.Light
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.Math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var firstTextView: TextView
    lateinit var textViewCentral: TextView
    lateinit var textViewFinal: TextView

    lateinit var sensorManager: SensorManager;
    lateinit var light_sensor: Sensor;
    lateinit var accelrometer: Sensor;

    private var color: Boolean = false
    private var lastUpdate: Long = 0L
    private var lastLumensUpdate: Float = 0F



    private lateinit var updatelight: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firstTextView = findViewById(R.id.textView)
        firstTextView.setBackgroundColor(Color.GREEN)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelrometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorManager.registerListener(this, accelrometer, SensorManager.SENSOR_DELAY_NORMAL)

            textViewCentral = findViewById(R.id.textViewCentral)


            val msg = getString(
                R.string.accelerometer_placeholder,
                accelrometer.resolution,
                accelrometer.maximumRange,
                accelrometer.vendor,
                accelrometer.name
            )
            textViewCentral.text = msg

        } else {
            textViewCentral = findViewById(R.id.textViewCentral)
            textViewCentral.setText(R.string.accelerometer_missing)
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            light_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
            sensorManager.registerListener(this, light_sensor, SensorManager.SENSOR_DELAY_NORMAL)
            textViewFinal = findViewById(R.id.textViewFinal)

            updatelight = getString(
                R.string.light_placeholder,
                light_sensor.resolution,
                light_sensor.maximumRange,
                light_sensor.vendor,
                light_sensor.name
            )

            textViewFinal.text = updatelight
            textViewFinal.setBackgroundColor(Color.YELLOW)


        } else {
            textViewFinal = findViewById(R.id.textViewFinal)
            textViewFinal.setText(R.string.light_missing)
        }

    }

    override fun onResume() {
        super.onResume()
        //Re register after on pause
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelrometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorManager.registerListener(this, accelrometer, SensorManager.SENSOR_DELAY_NORMAL)
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            light_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
            sensorManager.registerListener(this, light_sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                getAccelerometer(event)
            } else if (event.sensor.type == Sensor.TYPE_LIGHT) {
                getLightSensor(event)
            }
        }

    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Not yet implemented
    }

    private fun getAccelerometer(event: SensorEvent) {
        val values = event.values
        // Movement
        val x = values[0]
        val y = values[1]
        val z = values[2]
        val accelationSquareRoot = ((x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH))
        val actualTime = System.currentTimeMillis()
        if (accelationSquareRoot >= 2) {
            if (actualTime - lastUpdate < 200) {
                return
            }
            lastUpdate = actualTime
            Toast.makeText(this, R.string.shuffed, Toast.LENGTH_SHORT).show()
            if (color) {
                firstTextView.setBackgroundColor(Color.GREEN)
            } else {
                firstTextView.setBackgroundColor(Color.RED)
            }
            color = !color
        }
    }

    private fun getLightSensor(event: SensorEvent) {

        if (abs(lastLumensUpdate - event.values[0]) < 200) {
            return
        } else {
            lastLumensUpdate = event.values[0]
            val intensity_val: String = getIntensityString(event)
            updatelight += getString(R.string.new_value, event.values[0], intensity_val)
            textViewFinal.text = updatelight

        }


    }

    private fun getIntensityString(event: SensorEvent): String {
        val intensity_val: String = if (1000 <= event.values[0] && event.values[0] <= 2000) {
            getString(R.string.medium)
        } else if (event.values[0] < 1000) {
            getString(R.string.low)
        } else if (event.values[0] > 2000) {
            getString(R.string.high)
        } else {
            ""
        }
        return intensity_val
    }

}
