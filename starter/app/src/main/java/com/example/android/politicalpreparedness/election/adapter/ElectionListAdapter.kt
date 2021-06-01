package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ElectionItemBinding
import com.example.android.politicalpreparedness.network.models.Election


class ElectionListAdapter(
    private val clickListener: ElectionListener
) : ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {

    var elections: List<Election> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = elections.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val election = getItem(position)
        holder.bind(election,clickListener)
    }

}

//DONE: Create ElectionViewHolder
//DONE: Bind ViewHolder
class ElectionViewHolder(private val binding: ElectionItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(election:Election,clickListener: ElectionListener) {
        binding.election = election
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }

    //DONE: Add companion object to inflate ViewHolder (from)
    companion object {
        fun from(parent: ViewGroup):ElectionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ElectionItemBinding.inflate(layoutInflater)
            return ElectionViewHolder(binding)
        }
    }
}

//DONE: Create ElectionDiffCallback

class ElectionDiffCallback() : DiffUtil.ItemCallback<Election>() {

    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }
}

//DONE: Create ElectionListener
class ElectionListener(val clickListener:(election:Election)->Unit){
    fun onClick(election: Election) = clickListener(election)
}