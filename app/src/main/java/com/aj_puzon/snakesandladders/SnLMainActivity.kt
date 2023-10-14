package com.aj_puzon.snakesandladders

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView

class SnLMainActivity : AppCompatActivity() {
    private val startValue = 60;
    private val numCols = 5
    private var snl = IntArray(startValue)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // generate an array of numbers from 60 to 1
        // in a snake and ladders pattern
        snl = combineArrays(generateSnLPatternArrays(startValue, numCols))

        val gridView = findViewById<GridView>(R.id.table)
        val adapter = TextViewAdapter(this)
        gridView.adapter = adapter
    }

    fun generateSnLPatternArrays(maxValue: Int, numCols: Int): Array<IntArray> {
        val numRows = maxValue / numCols
        val arrays = Array(numRows) { IntArray(numCols) }
        var startValue = maxValue

        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                arrays[row][col] = startValue
                startValue--
            }
            if (row % 2 != 0) {
                arrays[row].reverse()
            }
        }

        return arrays
    }


    fun combineArrays(arrays: Array<IntArray>): IntArray {
        return arrays.flatMap { it.asIterable() }.toIntArray()
    }

    inner class TextViewAdapter(private val context: Context): BaseAdapter() {
        override fun getCount(): Int {
            return startValue
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val textView = TextView(context)
            val actualPosition = snl[position]

            // set the text to START at 1
            // set the text to END at 60
            // otherwise set the text to the reversed position
            if (actualPosition == 1) {
                textView.text = "START"
            } else if (actualPosition == startValue) {
                textView.text = "END"
            } else {
                textView.text = actualPosition.toString()
            }

//            // increase font size when it's not the start or end
//            if (actualPosition != 1 && actualPosition != startValue) {
//                textView.textSize = 30f
//            } else {
//                // otherwise set the font size to 20
//                textView.textSize = 20f
//            }
            
            // set the font size to 20
            textView.textSize = 20f

            // set the text alignment to center
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textView.gravity = View.TEXT_ALIGNMENT_CENTER

            // put a border on the text view
            textView.setBackgroundResource(R.drawable.border)

            // set the padding of the text view
            textView.setPadding(10, 10, 10, 10)

            // set the height of the text view
            textView.height = 100

            return textView
        }
    }
}