package com.example.roulette

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.os.Handler

class MainActivity : AppCompatActivity() {

    private lateinit var balanceText: TextView
    private lateinit var betAmount: EditText
    private lateinit var betChoice: EditText
    private lateinit var spinButton: Button
    private lateinit var resultText: TextView
    private lateinit var rouletteWheel: RouletteView
    private lateinit var colorChoiceGroup: RadioGroup
    private lateinit var allInButton: Button

    private var balance = 1000
    private val rouletteNumbers = arrayOf(0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        balanceText = findViewById(R.id.balanceText)
        betAmount = findViewById(R.id.betAmount)
        betChoice = findViewById(R.id.betChoice)
        spinButton = findViewById(R.id.spinButton)
        resultText = findViewById(R.id.resultText)
        rouletteWheel = findViewById(R.id.rouletteWheel)
        colorChoiceGroup = findViewById(R.id.colorChoiceGroup)
        allInButton = findViewById(R.id.allInButton)

        colorChoiceGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.greenOption) {
                betChoice.setText("0")
                betChoice.isEnabled = false
            } else {
                betChoice.isEnabled = true
            }
        }

        allInButton.setOnClickListener {
            betAmount.setText(balance.toString())
        }

        spinButton.setOnClickListener {
            val bet = betAmount.text.toString().toIntOrNull() ?: 0
            val betNumber = betChoice.text.toString().toIntOrNull()
            val chosenColor = when (colorChoiceGroup.checkedRadioButtonId) {
                R.id.greenOption -> "green"
                R.id.redOption -> "red"
                R.id.blackOption -> "black"
                else -> ""
            }

            if (bet > balance || bet <= 0 || betNumber == null || betNumber !in rouletteNumbers) {
                Toast.makeText(this, "insufficient funds or incorrect bid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            balance -= bet
            balanceText.text = "Balance: $$balance"
            spinButton.isEnabled = false
            spinRoulette(bet, chosenColor, betNumber)
        }
    }

    private fun spinRoulette(bet: Int, chosenColor: String, betNumber: Int) {
        val winningIndex = (0 until rouletteNumbers.size).random()
        val winningNumber = rouletteNumbers[winningIndex]
        val winningAngle = 360f * winningIndex / rouletteNumbers.size

        val rotate = RotateAnimation(
            0f, 3600f + winningAngle,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 3000
        rotate.fillAfter = true
        rotate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                showResult(bet, chosenColor, winningNumber, betNumber)
                spinButton.isEnabled = true
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        rouletteWheel.setRotationAngle(winningAngle)
        rouletteWheel.startAnimation(rotate)
    }

    private fun showResult(bet: Int, chosenColor: String, winningNumber: Int, betNumber: Int) {
        val winningColor = when {
            winningNumber == 0 -> "green"
            rouletteNumbers.indexOf(winningNumber) % 2 == 0 -> "red"
            else -> "black"
        }

        val winMultiplier = when {
            winningColor == "green" -> 15
            chosenColor == winningColor && winningNumber == betNumber -> 5
            chosenColor == winningColor || winningNumber == betNumber -> 2
            else -> 0
        }

        val winnings = bet * winMultiplier
        balance += winnings

        balanceText.text = "Balance: $$balance"
        resultText.text = "Result: $winningNumber $winningColor; Win: $$winnings"

        // Мигание выпавшей ячейки
        val winningSectorAngle = 360f * rouletteNumbers.indexOf(winningNumber) / rouletteNumbers.size
        rouletteWheel.setSectorHighlight(winningSectorAngle)

        if (balance <= 0) {
            Toast.makeText(this, "Balance exhausted!", Toast.LENGTH_SHORT).show()
            spinButton.isEnabled = false
        }
    }
}
