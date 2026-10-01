package com.silkysys.warehouse.app.picking.startPick.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.silkysys.warehouse.R
import android.widget.ImageView
import coil.load
import coil.size.Scale
import com.silkysys.warehouse.sharedcode.domain.picking.startPick.entity.StartPickEntity
import java.util.*
import kotlin.collections.ArrayList


class PickItemsAdapter(
    private val items: ArrayList<StartPickEntity.Fulfillment.Item>,
    private val mOnClickListener: View.OnClickListener
) : RecyclerView.Adapter<PickItemsAdapter.ViewHolder>() {
    private var filteredList: ArrayList<StartPickEntity.Fulfillment.Item> = ArrayList()
    private var tempList: ArrayList<StartPickEntity.Fulfillment.Item> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pick_items_row, parent, false)
        return ViewHolder(parent.context, view)
    }

    init {
        tempList.addAll(items)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(queryText: String) {
        items.clear()
        items.addAll(filteredList)
        if (queryText.isNotEmpty()) {
            // reset filtered list
            filteredList.clear()
            // after remove items reset it again
            if (items.isEmpty()) {
                items.addAll(tempList)
            }
            // loop to find item match query
            for (item in items) {
                if (item.barcode?.contains(queryText) == true ||
                    item.name.contains(queryText) ||
                    item.name.lowercase(Locale.ROOT).contains(queryText)
                ) {
                    filteredList.add(item)
                }
            }
            //to avoid redundancy
            items.clear()
            items.addAll(filteredList)
            notifyDataSetChanged()
        } else {
            // reset items to start again
            items.clear()
            items.addAll(tempList)
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.cardView.tag = item
        holder.titleTextView.text = item.name
        holder.subtitleTextView.text = item.description.orEmpty().let { description ->
            if (description.length < 25) description else description.take(22).plus(" ...")
        }
        holder.remainTextView.text = (item.quantity - item.picked_quantity).toString()
        holder.pickedTextView.text = item.picked_quantity.toString()
        holder.statusCardView.text = item.status
        holder.cardView.contentDescription =
            "${item.name}. Remaining ${item.quantity - item.picked_quantity}, picked ${item.picked_quantity}. ${item.status}"

        val url: String = item.image

        holder.imageView.load(url) {
            scale(Scale.FIT)
            placeholder(R.drawable.image_placeholder)
            error(R.drawable.image_placeholder)
        }

        holder.cardView.setOnClickListener(mOnClickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(val context: Context, view: View) : RecyclerView.ViewHolder(view) {
        val cardView: View = view.findViewById(R.id.cardView)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val subtitleTextView: TextView = view.findViewById(R.id.subtitleTextView)
        val remainTextView: TextView = view.findViewById(R.id.remainTextView)
        val pickedTextView: TextView = view.findViewById(R.id.pickedTextView)
        val statusCardView: TextView = view.findViewById(R.id.statusCardView)
    }
}
