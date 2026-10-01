package com.silkysys.warehouse.app.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.silkysys.warehouse.R
import com.silkysys.warehouse.domain.entity.MainMenu
import com.silkysys.warehouse.domain.entity.NoneMenuType
import com.silkysys.warehouse.domain.entity.ProgressMenuType
import java.util.*

class MainAdapter(var menus: ArrayList<MainMenu>): RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context)
            .inflate(R.layout.main_menu_item_layout, parent, false)
        return ViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = menus[position]

        holder.titleTextView.text = item.title
        holder.descriptionTextView.text = item.description
        holder.imageView.setImageResource(item.icon)
        holder.cardView.setOnClickListener(item.clickListener)
        holder.cardView.contentDescription = "${item.title}. ${item.description}"


        if (item is NoneMenuType) {
            // Do nothing
        } else if (item is ProgressMenuType) {
            if (item.total != 0) {
                val typeModel = item as ProgressMenuType
                val view = LayoutInflater.from(holder.context)
                    .inflate(
                        R.layout.progress_bar_menu_type_layout,
                        holder.typeContainerView,
                        false
                    )
                holder.typeContainerView.removeAllViews()
                holder.typeContainerView.addView(view)
                val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
                val totalTextView = view.findViewById<TextView>(R.id.totalTextView)
                progressBar.progress = typeModel.value
                progressBar.max = typeModel.total
                totalTextView.text = typeModel.value.toString() + "/" + typeModel.total.toString()
                progressBar.visibility= View.GONE
                totalTextView.visibility= View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        /// return size of the menus list
        return menus.size
    }

    class ViewHolder(val context: Context, view: View): RecyclerView.ViewHolder(view) {
        var cardView: MaterialCardView = view.findViewById(R.id.cardView)
        var titleTextView: TextView = view.findViewById(R.id.titleTextView)
        var descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        var imageView: ImageView = view.findViewById(R.id.imageView)
        var typeContainerView: LinearLayout = view.findViewById(R.id.typeContainerView)
    }

}
