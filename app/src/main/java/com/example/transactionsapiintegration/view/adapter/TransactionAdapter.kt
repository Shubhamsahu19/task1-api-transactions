package com.example.transactionsapiintegration.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.transactionsapiintegration.databinding.ItemTransactionBinding
import com.example.transactionsapiintegration.model.Transaction

class TransactionAdapter(private var originalList: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private var filteredList: List<Transaction> = originalList

    inner class ViewHolder(val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = filteredList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val item = filteredList[position]
            tvDate.text = item.date
            tvAmount.text = item.amount.toString()
            tvCategory.text = item.category
            tvDescription.text = item.description
        }
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.category.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun updateList(newList: List<Transaction>) {
        originalList = newList
        filteredList = newList
        notifyDataSetChanged()
    }
}
