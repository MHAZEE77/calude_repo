package com.silkysys.warehouse.app.picking.pick.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.silkysys.warehouse.R
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity
import android.widget.Checkable

class PickWaitingAdapter constructor(
    private var selectedItems: MutableList<PickEntity.Fulfillments.Waiting>,
    private var onItemCheckListener: OnItemCheckListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    constructor(checkListener: OnItemCheckListener,
                selectedItems: MutableList<PickEntity.Fulfillments.Waiting>):
            this(onItemCheckListener = checkListener,selectedItems=selectedItems)
    private var items: ArrayList<PickEntity.Fulfillments.Waiting> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.pick_waiting_header_item, parent, false)
                HeaderViewHolder(parent.context, view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.pick_waiting_item, parent, false)
                ViewHolder(parent.context, view)
            }
        }
    }
    interface OnItemCheckListener {
        fun onItemCheck(selectedList: MutableList<PickEntity.Fulfillments.Waiting>)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.checkbox.setOnClickListener {
                updateAllItemCheck(holder.checkbox)
            }
        } else if (holder is ViewHolder) {
            val item = items[position - 1]

            val isSelected = isItemSelected(item)
            holder.checkbox.isChecked = isSelected
            holder.cardView.isActivated = isSelected

            holder.idTextView.text = "#" + item.id
            holder.productTextView.text = item.total_items.toString()
            holder.orderTextView.text = item.name
            holder.cardView.contentDescription = "Order ${item.name}, ${item.total_items} items"

            holder.cardView.setOnClickListener {
                updateItemChecked(item, holder.checkbox, holder.cardView)
            }
            holder.checkbox.setOnClickListener {
                updateItemChecked(item, holder.checkbox, holder.cardView)
            }
        }
    }

    fun updateItemChecked(model: PickEntity.Fulfillments.Waiting, checkBox: CheckBox, cardView: View) {
        val wasSelected = isItemSelected(model)
        if (wasSelected) {
            selectedItems.remove(model)
        } else {
            selectedItems.add(model)
        }
        onItemCheckListener.onItemCheck(selectedItems)
        val newState = !wasSelected
        checkBox.isChecked = newState
        cardView.isActivated = newState
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0) { 1 } else { 2 }
    }

    override fun getItemCount(): Int {
        return items.size + 1
    }
    fun addAllItem(list: List<PickEntity.Fulfillments.Waiting>) {
        clearItems()
        items.addAll(list)
         onItemCheckListener.onItemCheck(selectedItems)
     }

    fun clearItems() {
         items.clear()
         onItemCheckListener.onItemCheck(selectedItems)
    }

    fun isItemSelected(model: PickEntity.Fulfillments.Waiting): Boolean {
        val item = selectedItems.firstOrNull { it.id == model.id }
        return item != null
    }

    fun updateAllItemCheck(checkBox: CheckBox) {
        if (selectedItems.size == items.size) {
            selectedItems.clear()
            checkBox.isChecked = false
        } else {
            selectedItems.clear()
            selectedItems.addAll(items)
            checkBox.isChecked = true
        }
        onItemCheckListener.onItemCheck(selectedItems)
        notifyDataSetChanged()
    }
    fun getSelectedList(): MutableList<PickEntity.Fulfillments.Waiting> {
        return selectedItems
    }

    inner class ViewHolder(val context: Context, view: View): RecyclerView.ViewHolder(view) {
        val rootView: View = view.findViewById(R.id.rootView)
        val cardView: MaterialCardView = view.findViewById(R.id.cardView)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)
        val idTextView: TextView = view.findViewById(R.id.idTextView)
        val productTextView: TextView = view.findViewById(R.id.productTextView)
        val orderTextView: TextView = view.findViewById(R.id.orderTextView)
    }
    inner class HeaderViewHolder(val context: Context, view: View): RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)
    }

}
