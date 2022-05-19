package com.esaudev.memorygame.ui.main

import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esaudev.memorygame.R
import com.esaudev.memorygame.StringUtils
import com.esaudev.memorygame.model.CR7Card
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {

    private val _gameList : MutableLiveData<List<CR7Card>> = MutableLiveData()
    val gameList : LiveData<List<CR7Card>>
        get() = _gameList

    private val _gamePaused : MutableLiveData<Boolean> = MutableLiveData()
    val gamePaused : LiveData<Boolean>
        get() = _gamePaused

    private val _pairFounded : MutableLiveData<Boolean> = MutableLiveData()
    val pairFounded : LiveData<Boolean>
        get() = _pairFounded

    private var _countDownTime = MutableLiveData<String>()
    val countDownTime: LiveData<String> = _countDownTime

    private var _hasTimerEnded = MutableLiveData(false)
    val hasTimerEnded : LiveData<Boolean> = _hasTimerEnded

    private var _playerHasWon = MutableLiveData(false)
    val playerHasWon: LiveData<Boolean> = _playerHasWon

    private var _isTimerPaused = MutableLiveData(false)
    val isTimerPaused : LiveData<Boolean> = _isTimerPaused

    private var turnActive: Boolean = true
    private var firstCardSelected: CR7Card? = null
    private var secondCardSelected: CR7Card? = null

    private var actionGameList: MutableList<CR7Card> = mutableListOf()

    private val defaultStartTime = 10000L
    var resumeFromMillis: Long = 0L

    init {
       startGame()
    }

    fun startGame() {
        val cardList = mutableListOf(
            CR7Card(
                image = R.drawable.cr7_1,
                founded = false
            ),
            CR7Card(
                image = R.drawable.cr7_2,
                founded = false
            ),
            CR7Card(
                image = R.drawable.cr7_3,
                founded = false
            ),
            CR7Card(
                image = R.drawable.cr7_4,
                founded = false
            ),
            CR7Card(
                image = R.drawable.cr7_5,
                founded = false
            ),
            CR7Card(
                image = R.drawable.cr7_6,
                founded = false
            ),
            CR7Card(
                image = R.drawable.cr7_7,
                founded = false
            ),
            CR7Card(
                image = R.drawable.cr7_8,
                founded = false
            )
        )
        val auxList: MutableList<CR7Card> = mutableListOf()
        cardList.forEach{
            auxList.add(it.copy(id = StringUtils.randomID()))
        }
        _gameList.value = (cardList + auxList).shuffled()
        _gamePaused.value = false
        actionGameList = _gameList.value!!.toMutableList()

        restartTimer()
    }

    private fun timer(millisInFuture:Long,countDownInterval:Long = 1000):CountDownTimer{
        return object: CountDownTimer(millisInFuture,countDownInterval){
            override fun onTick(millisUntilFinished: Long){

                if (_isTimerPaused.value == true) {
                    resumeFromMillis = millisUntilFinished
                    cancel()
                } else {
                    val minutes = if ((millisUntilFinished / 1000 / 60) > 9) "${millisUntilFinished / 1000 / 60}"
                    else "0${millisUntilFinished / 1000 / 60}"
                    val seconds = if ((millisUntilFinished / 1000 % 60) > 9) "${millisUntilFinished / 1000 % 60}"
                    else "0${millisUntilFinished / 1000 % 60}"

                    _countDownTime.value = "$minutes:$seconds"
                }
            }

            override fun onFinish() {
                _hasTimerEnded.value = true
            }
        }
    }

    fun pauseTimer() {
        _isTimerPaused.value = true
    }

    fun restartTimer() {
        _isTimerPaused.value = false
        _playerHasWon.value = false
        _hasTimerEnded.value = false
        timer(defaultStartTime).start()
    }

    fun performAction(cardSelected: CR7Card) {
        viewModelScope.launch {
            if (turnActive) {
                firstCardSelected = cardSelected
                actionGameList[actionGameList.indexOf(firstCardSelected)].founded = !actionGameList[actionGameList.indexOf(firstCardSelected)].founded
                _gameList.value = actionGameList
                turnActive = !turnActive
            } else {
                secondCardSelected = cardSelected
                actionGameList[actionGameList.indexOf(secondCardSelected)].founded = !actionGameList[actionGameList.indexOf(secondCardSelected)].founded
                _gameList.value = actionGameList
                turnActive = !turnActive
            }

            if (firstCardSelected != null && secondCardSelected != null) {
                _gamePaused.value = true
                _pairFounded.value = firstCardSelected!!.cardIndicator == secondCardSelected!!.cardIndicator
                Handler(Looper.getMainLooper()).postDelayed({
                    if (firstCardSelected!!.cardIndicator != secondCardSelected!!.cardIndicator) {
                        actionGameList[actionGameList.indexOf(firstCardSelected)].founded = false
                        actionGameList[actionGameList.indexOf(secondCardSelected)].founded = false
                        _gameList.value = actionGameList
                    }
                    firstCardSelected = null
                    secondCardSelected = null
                    _gamePaused.value = false

                    if (allCardsFounded(_gameList.value!!)) {
                        _playerHasWon.value = true
                        pauseTimer()
                    }
                }, 1500)
            }
        }
    }

    private fun allCardsFounded(cards: List<CR7Card>): Boolean {
        return cards.filter { it.founded }.size == cards.size
    }

}