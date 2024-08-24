package com.bignerdranch.android.geoquiz2

import android.app.Activity
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bignerdranch.android.geoquiz2.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val isAnswerShown = result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            quizViewModel.setCheaterForCurrentQuestion(isAnswerShown)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        setupListeners()
        updateQuestion()
    }

    private fun setupListeners() {
        binding.trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }

        binding.falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
        }

        binding.nextButton.setOnClickListener {
            moveToNext()
        }

        binding.cheatButton.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        binding.prevButton.setOnClickListener {
            moveToPrevious()
        }

        binding.nextQuestionTextView.setOnClickListener {
            moveToNext()
        }
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
        binding.trueButton.isEnabled = true
        binding.falseButton.isEnabled = true
    }

    private fun moveToNext() {
        if (quizViewModel.answeredCount == quizViewModel.getQuestionBankSize()) {
            showScore()
            return
        }
        quizViewModel.moveToNext()
        updateQuestion()
    }

    private fun moveToPrevious() {
        quizViewModel.moveToPrevious()
        updateQuestion()
    }

    private fun checkAnswer(userAnswer: Boolean) {
        if (!binding.trueButton.isEnabled && !binding.falseButton.isEnabled) {
            return
        }
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = when {
            quizViewModel.isCheaterForCurrentQuestion() -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        binding.trueButton.isEnabled = false
        binding.falseButton.isEnabled = false
        quizViewModel.answeredCount++

        if (userAnswer == correctAnswer) {
            quizViewModel.incrementCorrectAnswers()
        }

        if (quizViewModel.answeredCount == quizViewModel.getQuestionBankSize()) {
            showScore()
        }
    }

    private fun showScore() {
        val percentage =
            (quizViewModel.correctAnswers.toDouble() / quizViewModel.getQuestionBankSize()) * 100
        val message = "You scored ${percentage.toInt()}%!"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}


