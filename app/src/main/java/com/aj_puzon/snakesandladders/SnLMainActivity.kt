package com.aj_puzon.snakesandladders

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SnLMainActivity : AppCompatActivity() {
    private val fullFinishAudio = false
    private val leaveTrail = false
    private val maxValue = 60
    private val numCols = 5
    private var snl = IntArray(maxValue)
    private var diceView: ImageView? = null
    private var rollBtn: Button? = null
    private var resetBtn: Button? = null
    private var progress: Int = 0;
    private var MP: MediaPlayer? = null;

    // coroutine for the delay
    private val myScope = CoroutineScope(Dispatchers.Main);

    override fun onDestroy() {
        super.onDestroy()

        // cancel the coroutine
        myScope.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.snl_activity_main)

        // generate an array of numbers from 60 to 1
        // in a snake and ladders pattern
        snl = generateSnLPattern(maxValue, numCols)

        // assign the grid view it's text view adapter
        val gridView = findViewById<GridView>(R.id.table)
        val adapter = TextViewAdapter(this)
        gridView.adapter = adapter

        // assign the dice view
        diceView = findViewById(R.id.diceView)

        // assign the roll dice button
        rollBtn = findViewById(R.id.rollBtn)

        // assign the reset button
        resetBtn = findViewById(R.id.resetBtn)
        
        // set the on click listener for the roll dice btn
        rollBtn!!.setOnClickListener {
            rollDice()
        }

        // set the on click listener for the reset dice btn
        resetBtn!!.setOnClickListener {
            reset()
        }
    }

    fun updateBoxes(isDelayed: Boolean = true) {
        // launch a new coroutine
        myScope.launch {
            // delay this coroutine for 800 miliseconds
            // if isDelayed is true
            if (isDelayed) {
                delay(800)
            }

            // do operation...
            for (i in 0 until maxValue) {
                // find the box
                val box = findViewById<TextView>(snl[i])

                if (leaveTrail) {
                    // change the background resource to selected
                    // if the box's id is less than or equal to the progress
                    if (box.id <= progress) {
                        box.setBackgroundResource(R.drawable.selected)
                        // set the text color of the textview box to white
                        box.setTextColor(resources.getColor(R.color.white))
                    } else {
                        box.setBackgroundResource(R.drawable.unselected)

                        // set the text color of the textview box to black
                        box.setTextColor(resources.getColor(R.color.black))
                    }
                } else {
                    // change the background resource of the previous box to unselected
                    // if the box's id is less than to the progress
                    if (box.id != progress) {
                        box.setBackgroundResource(R.drawable.unselected)

                        // set the text color of the textview box to black
                        box.setTextColor(resources.getColor(R.color.black))
                    } else if (box.id == progress) {
                        box.setBackgroundResource(R.drawable.selected)

                        // set the text color of the textview box to white
                        box.setTextColor(resources.getColor(R.color.white))
                    }
                }
            }
        }
    }

    fun reset() {
        // reset position
        progress = 0;

        // reset boxes and disable delay
        updateBoxes(false)

        // reset dice
        updateDice(6);

        // stop audio
        MP!!.seekTo(0)
    }

    fun animateDice() {
        // Create ObjectAnimator for translation (move from left to center)
        val translationAnimator = ObjectAnimator.ofFloat(
            diceView,
            View.TRANSLATION_Y,
            -700f,
            0f
        )

        // Create ObjectAnimator for rotation (rolling animation)
        val rotationAnimator = ObjectAnimator.ofFloat(
            diceView,
            View.ROTATION,
            0f,
            360f
        )

        // Create ObjectAnimator for alpha (fade animation)
        val fadeAnimator = ObjectAnimator.ofFloat(
            diceView,
            View.ALPHA, // Use View.ALPHA to animate alpha (fade)
            0f, 1f // Fade from 0 (invisible) to 1 (fully visible)
        )

        // Set the animation duration
        translationAnimator.duration = 1000
        rotationAnimator.duration = 1000
        fadeAnimator.duration = 500

        // Create an AnimatorSet to play both animations together
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translationAnimator, rotationAnimator)

        // Start the animations
        animatorSet.start()
    }

    fun updateDice(value: Int) {
        when (value) {
            1 -> diceView!!.setImageResource(R.drawable.dice_1)
            2 -> diceView!!.setImageResource(R.drawable.dice_2)
            3 -> diceView!!.setImageResource(R.drawable.dice_3)
            4 -> diceView!!.setImageResource(R.drawable.dice_4)
            5 -> diceView!!.setImageResource(R.drawable.dice_5)
            6 -> diceView!!.setImageResource(R.drawable.dice_6)
        }
    }

    fun endAudio() {
        var audio: Int? = null
        if (fullFinishAudio) {
            audio = R.raw.full_finish
        } else {
            audio = R.raw.finish
        }

        MP = MediaPlayer.create(this, audio);
        MP!!.start()
    }

    fun rollAudio() {
        MP = MediaPlayer.create(this, R.raw.transition);
        MP!!.start()
    }

    fun rollDice () {
        // generate a random number from 1 to 5
        val diceValue = (1..6).random()
        Log.i("Dice Value", diceValue.toString())

        // update the dice view
        updateDice(diceValue)

        // update the progress
        progress += diceValue

        // if the progress is higher than the max value
        // get the excess by subtracting the max value to the progress value
        if (progress > maxValue) {
            Log.i("Excess detected!", "")
            Log.i("Progress before subtracting excess: ", progress.toString())
            val excess = progress - maxValue
            progress = maxValue - excess
        }
        Log.i("Progress", progress.toString())

        // if the progress is equal to the max value
        // play the end audio
        if (progress == maxValue) {
            endAudio()
        } else {
            // else play the roll audio
            rollAudio()
        }

        // animate dice
        animateDice()

        // update the boxes
        updateBoxes()
    }

    fun generateSnLPattern(maxValue: Int, numCols: Int): IntArray {
        // divide maxValue to get the number of rows
        val numRows = maxValue / numCols

        // define the multi dimensional array
        val arrays = Array(numRows) { IntArray(numCols) }

        // start value as max value
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

        // combine the multidimensional arrays into one
        val combinedArrays = arrays.flatMap { it.asIterable() }.toIntArray()

        return combinedArrays
    }

    inner class TextViewAdapter(private val context: Context): BaseAdapter() {
        override fun getCount(): Int {
            return maxValue
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
            } else if (actualPosition == maxValue) {
                textView.text = "END"
            } else {
                textView.text = actualPosition.toString()
            }
            
            // set the font size to 20
            textView.textSize = 20f

            // set the text alignment to center
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textView.gravity = View.TEXT_ALIGNMENT_CENTER

            // set the background resource to unselected with border on the text view
            textView.setBackgroundResource(R.drawable.unselected)

            // set the padding of the text view
            textView.setPadding(10, 10, 10, 10)

            // set the height of the text view
            textView.height = 100

            // set the id of the text view
            textView.id = actualPosition

            return textView
        }
    }
}