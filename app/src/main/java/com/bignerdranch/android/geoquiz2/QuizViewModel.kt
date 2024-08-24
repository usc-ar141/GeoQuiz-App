package com.bignerdranch.android.geoquiz2

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val IS_CHEATER_MAP_KEY = "IS_CHEATER_MAP_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    private val isCheaterMap: MutableMap<Int, Boolean>
        get() = savedStateHandle.get<MutableMap<Int, Boolean>>(IS_CHEATER_MAP_KEY) ?: mutableMapOf()

    var currentIndex: Int
        get() = savedStateHandle.get(CURRENT_INDEX_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    var answeredCount = 0
    var correctAnswers = 0

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrevious() {
        currentIndex = if (currentIndex - 1 < 0) {
            questionBank.size - 1
        } else {
            currentIndex - 1
        }
    }

    fun getQuestionBankSize() = questionBank.size

    fun incrementCorrectAnswers() {
        correctAnswers++
    }

    fun setCheaterForCurrentQuestion(isCheater: Boolean) {
        val questionIndex = currentIndex
        isCheaterMap[questionIndex] = isCheater
        savedStateHandle.set(IS_CHEATER_MAP_KEY, isCheaterMap)
    }

    fun isCheaterForCurrentQuestion(): Boolean {
        val questionIndex = currentIndex
        return isCheaterMap[questionIndex] ?: false
    }
}
