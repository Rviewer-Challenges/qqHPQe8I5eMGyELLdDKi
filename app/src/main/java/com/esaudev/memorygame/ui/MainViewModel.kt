package com.esaudev.memorygame.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esaudev.memorygame.R
import com.esaudev.memorygame.StringUtils
import com.esaudev.memorygame.model.CR7Card
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Handler
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

    private var turnActive: Boolean = true
    private var firstCardSelected: CR7Card? = null
    private var secondCardSelected: CR7Card? = null

    private var actionGameList: MutableList<CR7Card> = mutableListOf()

    init {
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
                android.os.Handler().postDelayed({
                    if (firstCardSelected!!.cardIndicator != secondCardSelected!!.cardIndicator) {
                        actionGameList[actionGameList.indexOf(firstCardSelected)].founded = false
                        actionGameList[actionGameList.indexOf(secondCardSelected)].founded = false
                        _gameList.value = actionGameList
                    }
                    firstCardSelected = null
                    secondCardSelected = null
                    _gamePaused.value = false
                }, 2000)
            }
        }
    }

}