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

    private lateinit var dialogGameWon: GameWonDialogFragment
    private lateinit var dialogGameLost: GameLostDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDialogs()
        initRecyclerView()
        initObservers()
    }

    private fun initDialogs() {
        dialogGameWon = GameWonDialogFragment()
        dialogGameLost = GameLostDialogFragment()
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
                dialogGameWon.onRestartClick = {
                    viewModel.startGame()
                }
                dialogGameWon.isCancelable = false
                if (!dialogGameWon.isAdded) {
                    dialogGameWon.show(supportFragmentManager, "game_won_dialog")
                }
            }
        }
        viewModel.hasTimerEnded.observe(this) { playerHasLost ->
            if (playerHasLost) {
                dialogGameLost.onRestartClick = {
                    viewModel.startGame()
                }
                dialogGameLost.isCancelable = false
                if (!dialogGameLost.isAdded) {
                    dialogGameLost.show(supportFragmentManager, "game_lost_dialog")
                }
            }
        }
        viewModel.gameList.observe(this) {
            cardListAdapter.counter = 0
            Log.d("CR7", "Observer triggered")
            cardListAdapter.submitList(it.toMutableList())
            cardListAdapter.notifyDataSetChanged()
        }
        viewModel.gamePaused.observe(this) { gamePaused ->
            if (gamePaused) {
                cardListAdapter.counter += 1
                cardListAdapter.setOnCardClickListener {
                    cardListAdapter.counter += 1
                }
            } else {
                cardListAdapter.counter += 1
                cardListAdapter.setOnCardClickListener {
                    Log.d("CR7", "Card click triggered")
                    viewModel.performActionWithAnimation(cardSelected = it)
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

    override fun onPause() {
        if (dialogGameWon.isAdded) {
            dialogGameWon.dismiss()
        }

        if (dialogGameLost.isAdded) {
            dialogGameLost.dismiss()
        }
        super.onPause()
    }
}