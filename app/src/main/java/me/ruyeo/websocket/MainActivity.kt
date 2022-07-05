package me.ruyeo.websocket

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.DropBoxManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import me.ruyeo.websocket.databinding.ActivityMainBinding
import me.ruyeo.websocket.model.BitCoin
import java.security.KeyStore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var webSocketBtc: WebSocketBtc
    private var lineValues = ArrayList<Entry>()
    private var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        webSocketBtc = WebSocketBtc()

        configureLineChart()
        setupUI()
    }

    private fun setupUI() {

        webSocketBtc.connectToSocket("live_trades_btcusd")
        webSocketBtc.socketListener(object : SocketListener {
            override fun onSuccess(bitCoin: BitCoin) {
                count++
                runOnUiThread {
                    if (bitCoin.event == "bts:subscription_succeeded") {
                        Toast.makeText(this@MainActivity, "Successfully Connected,Wait a moment", Toast.LENGTH_SHORT).show()
                    } else {
                        lineValues.add(Entry(count.toFloat(),bitCoin.data.price.toFloat()))
                        setLineChartData(lineValues)
                    }
                }
            }
            override fun onFailure(message: String) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun setLineChartData(
        pricesHigh: ArrayList<Entry>
    ) {
        val dataSets: ArrayList<ILineDataSet> = ArrayList()

        val highLineDataSet = LineDataSet(pricesHigh, "Live" )
        highLineDataSet.setDrawCircles(true)
        highLineDataSet.circleRadius = 4f
        highLineDataSet.setDrawValues(false)
        highLineDataSet.lineWidth = 3f
        highLineDataSet.color = Color.GREEN
        highLineDataSet.setCircleColor(Color.GREEN)
        dataSets.add(highLineDataSet)

        val lineData = LineData(dataSets)
        binding.lineChart.data = lineData
        binding.lineChart.invalidate()
    }

    private fun configureLineChart() {
        val desc = Description()
        desc.text = "BTC-USD"
        desc.textSize = 20F
        binding.lineChart.description = desc
        val xAxis: XAxis = binding.lineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            @RequiresApi(Build.VERSION_CODES.N)
            private val mFormat: SimpleDateFormat = SimpleDateFormat("HH mm", Locale.getDefault())
            @RequiresApi(Build.VERSION_CODES.N)
            override fun getFormattedValue(value: Float): String {
                return mFormat.format(Date(System.currentTimeMillis()))
            }
        }
    }

    private fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("HH mm")
            val netDate = Date(s.toLong() * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }
}