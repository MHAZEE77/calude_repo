package com.silkysys.warehouse.app.picking.pick.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.silkysys.warehouse.R
import com.silkysys.warehouse.sharedcode.domain.picking.pick.entity.PickEntity

import java.util.ArrayList

class PickStartedAdapter(
    private val mOnClickListener: View.OnClickListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Properties
    private var items: ArrayList<PickEntity.Fulfillments.Started> = ArrayList()

    // Override Methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.pick_started_header_item, parent, false)
                HeaderViewHolder(parent.context, view)
            }
             else -> {
                 val view = LayoutInflater.from(parent.context)
                     .inflate(R.layout.pick_starte_item, parent, false)
                 ViewHolder(parent.context, view)
             }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
             val item = items[position - 1]
            holder.rootView.tag = item
            holder.idTextView.text = "#" + item.id
            holder.orderName.text = item.name
            holder.totalTextView.text = item.total_items_picked_quantity.toString() + "/" + item.total_items_quantity.toString()

            holder.statusTextView.text = item.status

            // set on click listener
            holder.rootView.setOnClickListener(mOnClickListener)
            // Style status Color
            when (item.status) {
                 "picking"-> {
                    holder.statusTextView.setTextColor (holder.context.resources.getColor(R.color.warning))
//                    holder.statusTextView.setBackgroundColor(holder.context.resources.getColor(R.color.warningStatusBackground))
                    holder.statusTextView.text = holder.context.getString(R.string.picking_label)
                }
                "ready_picking" -> {
                    holder.statusTextView.setTextColor (holder.context.resources.getColor(R.color.colorAccent))
//                    holder.statusTextView.setBackgroundColor(holder.context.resources.getColor(R.color.successStatusBackground))
                    holder.statusTextView.text = holder.context.getString(R.string.ready_picking)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0) { 1 } else { 2 }
    }

    override fun getItemCount(): Int {
        return items.size + 1
    }

    /**
     *
     * Private Methods
     */

//    fun addItem(model: PickEntity.Fulfillments.Started){
//        items.add(model)
//        notifyItemInserted(items.lastIndex)
//    }

    fun addAllItem(list: List<PickEntity.Fulfillments.Started>) {
        clearItems()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun clearItems(){
        items.clear()
        notifyDataSetChanged()
    }

    /**
     *
     * Inner Class
     */

    inner class ViewHolder(val context: Context, view: View): RecyclerView.ViewHolder(view) {
        val rootView: View = view.findViewById(R.id.rootView)
        val cardView: View = view.findViewById(R.id.cardView)
        val idTextView: TextView = view.findViewById(R.id.idTextView)
        val statusTextView: TextView = view.findViewById(R.id.statusTextView)
        val orderName: TextView = view.findViewById(R.id.productTextView)
        val totalTextView: TextView = view.findViewById(R.id.totalTextView)
    }

    inner class HeaderViewHolder(val context: Context, view:View): RecyclerView.ViewHolder(view)

}
