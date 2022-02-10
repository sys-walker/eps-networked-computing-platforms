package com.example.miniactivity1

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity(),SensorEventListener{
    private lateinit var sensorManager:SensorManager;
    private var color:Boolean = false;
    private lateinit var view: TextView;
    private var lastUpdate:Long =0L; //    private long lastUpdate;

    /** Called when the activity is first created. */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        view = findViewById(R.id.textView)
        view.setBackgroundColor(Color.GREEN)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            //1a bona practica
            sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
            // register this class as a listener for the accelerometer sensor
            lastUpdate = System.currentTimeMillis()
        }else{
            System.out.println("No accelerometer")
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event !=null){
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
                    view.setBackgroundColor(Color.GREEN)
                } else {
                    view.setBackgroundColor(Color.RED)
                }
                color = !color
            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }


}