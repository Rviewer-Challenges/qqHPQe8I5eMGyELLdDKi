package com.esaudev.memorygame.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.esaudev.memorygame.R
import com.esaudev.memorygame.StringUtils
import com.esaudev.memorygame.databinding.ActivityMainBinding
import com.esaudev.memorygame.model.CR7Card
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var cardListAdapter: CardListAdapter = CardListAdapter()

    private val cardList = mutableListOf(
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

    lateinit var gameList: List<CR7Card>

    private var turnActive: Boolean = true
    private var firstCardSelected: CR7Card? = null
    private var secondCardSelected: CR7Card? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auxList: MutableList<CR7Card> = mutableListOf()
        cardList.forEach{
            auxList.add(it.copy(id = StringUtils.randomID()))
        }
        gameList = (cardList + auxList).shuffled()
        initRecyclerView()
        initListeners()
        initGame()
    }

    private fun initRecyclerView() {
        with(binding.rvMemoryGame) {
            adapter = cardListAdapter
            layoutManager = GridLayoutManager(this@MainActivity,4, LinearLayoutManager.VERTICAL, false)
            overScrollMode = View.OVER_SCROLL_NEVER
            setHasFixedSize(false)
        }
    }

    private fun initListeners() {

        cardListAdapter.setOnCardClickListener {
            Log.d("CR7", "Card click triggered")
            performCardAction(cardSelected = it)
        }
    }

    private fun performCardAction(cardSelected: CR7Card) {
        if (turnActive) {
            firstCardSelected = cardSelected
            gameList[gameList.indexOf(firstCardSelected)].founded =
                !gameList[gameList.indexOf(firstCardSelected)].founded
            cardListAdapter.submitList(gameList.toMutableList())
            cardListAdapter.notifyDataSetChanged()
            turnActive = !turnActive
        } else {
            secondCardSelected = cardSelected
            gameList[gameList.indexOf(secondCardSelected)].founded =
                !gameList[gameList.indexOf(secondCardSelected)].founded
            cardListAdapter.submitList(gameList.toMutableList())
            cardListAdapter.notifyDataSetChanged()
            turnActive = !turnActive
        }

        if (firstCardSelected != null && secondCardSelected != null) {
            if (firstCardSelected!!.cardIndicator != secondCardSelected!!.cardIndicator) {
                gameList[gameList.indexOf(firstCardSelected)].founded = false
                gameList[gameList.indexOf(secondCardSelected)].founded = false
                cardListAdapter.submitList(gameList.toMutableList())
                cardListAdapter.notifyDataSetChanged()
            }
            firstCardSelected = null
            secondCardSelected = null
        }
    }

    private fun initGame() {

        cardListAdapter.submitList(gameList)
    }

}