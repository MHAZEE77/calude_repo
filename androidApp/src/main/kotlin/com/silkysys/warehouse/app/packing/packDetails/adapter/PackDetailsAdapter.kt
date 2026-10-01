package com.silkysys.warehouse.app.packing.packDetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.silkysys.warehouse.R
import com.silkysys.warehouse.sharedcode.domain.packing.packDetails.entity.PackDetailsEntity

/**
 *
 * Created by: Mahmoud Ibrahim on 5/7/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
class PackDetailsAdapter(private val packListItems: ArrayList<PackDetailsEntity.PackList>) :
    RecyclerView.Adapter<PackDetailsAdapter.PackDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackDetailsViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.pack_details_item,parent,false)
        return PackDetailsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return packListItems.size
    }

    override fun onBindViewHolder(holder: PackDetailsViewHolder, position: Int) {
         holder.name.text = packListItems[position].name
        holder.height.text = packListItems[position].height.toString()
        holder.length.text = packListItems[position].length.toString()
        holder.width.text = packListItems[position].width.toString()
        holder.weight.text = packListItems[position].weight.toString()
        holder.uom.text = packListItems[position].uom
        holder.quantity.text = packListItems[position].quantity.toString()
        if (packListItems[position].isFragile==1)
            holder.isFragile.text = holder.itemView.context.getString(R.string.yes)
        else
            holder.isFragile.text =holder.itemView.context.getString(R.string.no)
    }

    class PackDetailsViewHolder(itemView: View): ViewHolder(itemView){
        val name: TextView= itemView.findViewById(R.id.tv_PD_Name)
        val height: TextView= itemView.findViewById(R.id.tv_PD_Height)
        val length: TextView= itemView.findViewById(R.id.tv_PD_Length)
        val width: TextView= itemView.findViewById(R.id.tv_PD_Width)
        val weight: TextView= itemView.findViewById(R.id.tv_PD_Weight)
        val uom: TextView= itemView.findViewById(R.id.tv_uom)
        val quantity: TextView= itemView.findViewById(R.id.tv_quantity)
        val isFragile: TextView= itemView.findViewById(R.id.tv_isFragile)
    }
}
