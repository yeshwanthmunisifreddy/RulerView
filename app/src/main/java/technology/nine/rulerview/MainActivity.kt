package technology.nine.rulerview

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import technology.nine.ruler.RulerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rulerview = findViewById<RulerView>(R.id.ruler)
        rulerview.setAlphaEnable(true)
        rulerview.setDefaultSelectedValue(78f)
        rulerview.setMinValue(50f)
        rulerview.setMaxValue(100f)
        rulerview.setIndicatorType(RulerView.LINE)
        rulerview.setItemSpacing(10)
        rulerview.setMaxLineColor(ContextCompat.getColor(this, R.color.knight_blue))
        rulerview.setMaxLineHeight(39)
        rulerview.setMaxLineWidth(3)
        rulerview.setMiddleLineColor(Color.parseColor("#444444"))
        rulerview.setMiddleLineHeight(18)
        rulerview.setMiddleLineWidth(3)
        rulerview.setMinLineColor(Color.parseColor("#444444"))
        rulerview.setMinLineHeight(18)
        rulerview.setMinLineWidth(3)
        rulerview.setResultTextColor(Color.parseColor("#444444"))
        rulerview.setResultTextSize(20)
        rulerview.setScaleTextColor(ContextCompat.getColor(this, R.color.knight_blue))
        rulerview.setScaleTextSize(24)

        rulerview.setChooseValueChangeListener(object : RulerView.OnChooseResulterListener {
            override fun onChooseValueChange(value: Float) {
                Log.e("Choosen value", "$value")
            }
        })

    }
}