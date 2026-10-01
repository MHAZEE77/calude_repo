package com.silkysys.warehouse.app.autentcation.main.adapter

/**
 *
 * Created by: Mahmoud Ibrahim on 4/10/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.silkysys.warehouse.R

internal class GridRVAdapter(
    private val courseList: List<GridViewModal>,
    private val context: Context
) :
    BaseAdapter() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var serviceTV: TextView
    private lateinit var serviceIV: ImageView

    override fun getCount(): Int {
        return courseList.size
    }
    override fun getItem(position: Int): GridViewModal = courseList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = convertView ?: layoutInflater.inflate(R.layout.grid_item, parent, false)
        serviceIV = itemView.findViewById(R.id.idIVService)
        serviceTV = itemView.findViewById(R.id.idTVService)
        serviceIV.setImageResource(courseList[position].serviceImg)
        serviceTV.text = courseList[position].serviceName
        return itemView
    }
}
