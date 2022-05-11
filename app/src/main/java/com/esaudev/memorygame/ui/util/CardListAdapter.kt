package com.esaudev.memorygame.ui.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.esaudev.memorygame.R
import com.esaudev.memorygame.databinding.ItemCardBinding
import com.esaudev.memorygame.model.CR7Card

class CardListAdapter(

): ListAdapter<CR7Card, BaseListViewHolder<*>>(DiffUtilCallback) {

    private object DiffUtilCallback : DiffUtil.ItemCallback<CR7Card>() {
        override fun areItemsTheSame(oldItem: CR7Card, newItem: CR7Card): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CR7Card, newItem: CR7Card): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseListViewHolder<*> {
        val itemBinding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BindViewHolderList(itemBinding)
    }

    override fun onBindViewHolder(holder: BaseListViewHolder<*>, position: Int) {
        when (holder) {
            is BindViewHolderList -> holder.bind(getItem(position), position)
        }
    }

    inner class BindViewHolderList(private val binding: ItemCardBinding) : BaseListViewHolder<CR7Card>(binding.root) {

        override fun bind(item: CR7Card, position: Int) = with(binding) {

            if (item.founded) {
                ivCard.setImageResource(item.image)
            } else {
                ivCard.setImageResource(R.drawable.cr7_0)
            }

            ivCard.setOnClickListener {
                if (!item.founded) {
                    onCardClickListener?.let { click ->
                        click(item)
                    }
                }
            }
        }
    }

    private var onCardClickListener: ((CR7Card) -> Unit)? = null

    fun setOnCardClickListener(listener: (CR7Card) -> Unit) {
        onCardClickListener = listener
    }

}