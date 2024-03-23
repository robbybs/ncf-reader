package com.rbs.nfcreader.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rbs.nfcreader.data.model.Card
import com.rbs.nfcreader.databinding.ItemCardBinding

class CardAdapter : ListAdapter<Card, CardAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) holder.bind(data)
    }

    inner class MyViewHolder(private val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Card) {
            with(binding) {
                val serialNumber = data.serialNumber
                val message = data.message

                tvSerialNumber.text = serialNumber
                tvMessages.text = message

                btnDelete.setOnClickListener {
                    onItemClickCallback?.onDeleteItem(data.id)
                }

                btnSend.setOnClickListener {
                    if (serialNumber != null && message != null) onItemClickCallback?.onSendItem(
                        serialNumber,
                        message
                    )
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onDeleteItem(id: Int)

        fun onSendItem(serialNumber: String, message: String)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Card>() {
            override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}