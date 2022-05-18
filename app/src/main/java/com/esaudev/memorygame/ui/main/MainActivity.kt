package com.esaudev.memorygame.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.esaudev.memorygame.databinding.ActivityMainBinding
import com.esaudev.memorygame.ui.components.GameLostDialogFragment
import com.esaudev.memorygame.ui.components.GameWonDialogFragment
import com.esaudev.memorygame.ui.util.CardListAdapter
import dagger.hilt.android.AndroidEntryPoint

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
        viewModel.countDownTime.observe(this) {
            binding.tvTimer.text = it
        }
        viewModel.playerHasWon.observe(this) { playerHasWon ->
            if (playerHasWon) {
                val dialog = GameWonDialogFragment()
                dialog.onRestartClick = {
                    viewModel.startGame()
                }
                dialog.isCancelable = false
                dialog.show(supportFragmentManager, "game_won_dialog")
            }
        }
        viewModel.hasTimerEnded.observe(this) { playerHasLost ->
            if (playerHasLost) {
                val dialog = GameLostDialogFragment()
                dialog.onRestartClick = {
                    viewModel.startGame()
                }
                dialog.isCancelable = false
                dialog.show(supportFragmentManager, "game_lost_dialog")
            }
        }
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
                Toast.makeText(this, "¡Encontraste un par SIUUUU!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "¡Sigue buscando!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}