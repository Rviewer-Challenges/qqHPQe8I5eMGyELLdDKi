package com.esaudev.memorygame.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.esaudev.memorygame.R
import com.esaudev.memorygame.StringUtils
import com.esaudev.memorygame.databinding.ActivityMainBinding
import com.esaudev.memorygame.model.CR7Card
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var cardListAdapter: CardListAdapter = CardListAdapter()

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        initObservers()
    }

    private fun initRecyclerView() {
        with(binding.rvMemoryGame) {
            adapter = cardListAdapter
            layoutManager = GridLayoutManager(this@MainActivity,4, LinearLayoutManager.VERTICAL, false)
            overScrollMode = View.OVER_SCROLL_NEVER
            setHasFixedSize(false)
        }
    }

    private fun initObservers() {
        viewModel.gameList.observe(this) {
            Log.d("CR7", "Observer triggered")
            cardListAdapter.submitList(it.toMutableList())
            cardListAdapter.notifyDataSetChanged()
        }
        viewModel.gamePaused.observe(this) { gamePaused ->
            if (gamePaused) {
                cardListAdapter.setOnCardClickListener {  }
            } else {
                cardListAdapter.setOnCardClickListener {
                    Log.d("CR7", "Card click triggered")
                    viewModel.performAction(cardSelected = it)
                }
            }
        }
        viewModel.pairFounded.observe(this) { pairFounded ->
            if (pairFounded) {
                Toast.makeText(this, "Encontraste un par!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sigue buscando!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}