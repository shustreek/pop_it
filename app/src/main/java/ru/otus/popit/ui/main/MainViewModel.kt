package ru.otus.popit.ui.main

import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.otus.popit.SingleLiveEvent

class MainViewModel : ViewModel() {

    private var popId: Int = 0
    private var soundPool: SoundPool? = null
    private lateinit var matrix: Array<Boolean>
    private var gameState = GameState.Stopped
    private var currentClickPop = 0

    private var startTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            mTimerState.value = (System.currentTimeMillis() - startTime) / 1000f
            handler.postDelayed(this, 100)
        }
    }

    private val mState = MutableLiveData<Array<Boolean>>()
    private val mCellStateByIndex: MutableLiveData<Pair<Int, Boolean>> = SingleLiveEvent()
    private val mTimerState = MutableLiveData<Float>()

    val state: LiveData<Array<Boolean>> = mState
    val cellStateByIndex: LiveData<Pair<Int, Boolean>> = mCellStateByIndex
    val timerState: LiveData<Float> = mTimerState

    init {
        initGame()
    }

    fun initSound(fd: AssetFileDescriptor) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build().apply {
                popId = load(fd, 1)
            }
    }

    private fun initGame() {
        gameState = GameState.Stopped
        matrix = Array(25) { true }
        mState.value = matrix
        mTimerState.value = 0f
        currentClickPop = 0
    }

    fun onPopClick(index: Int) {
        currentClickPop++

        when (gameState) {
            GameState.Started -> {
                if (currentClickPop == matrix.size) {
                    gameState = GameState.Ended
                    handler.removeCallbacks(runnable)
                }
            }
            GameState.Stopped -> {
                gameState = GameState.Started
                startTime = System.currentTimeMillis()
                handler.post(runnable)
            }
            GameState.Ended -> {
            }
        }

        if (popId > 0) {
            soundPool?.play(popId, 1f, 1f, 1, 0, 1f)
        }
        matrix[index] = false
        mCellStateByIndex.value = index to false
    }

    fun onUpdateClick() {
        initGame()
        handler.removeCallbacks(runnable)
    }
}

enum class GameState {
    Started,
    Stopped,
    Ended
}