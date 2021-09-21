package ru.otus.popit.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private lateinit var matrix: Array<Boolean>

    private val mState = MutableLiveData<Array<Boolean>>()

    val state: LiveData<Array<Boolean>> = mState

    init {
        initGame()
    }

    private fun initGame() {
        matrix = Array(25) { true }
        mState.value = matrix
    }

    fun onPopClick(index: Int) {

    }
}