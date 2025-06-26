package com.tvm.doctorcube.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tvm.doctorcube.R
import com.tvm.doctorcube.HomeFragment.SearchItem

class SearchResultsAdapter(
    private var searchItems: MutableList<SearchItem?>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    fun updateData(newItems: MutableList<SearchItem?>) {
        searchItems = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_search_result_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = searchItems[position] ?: return
        holder.textView?.text = item.title
        when (item.type) {
            "University" -> holder.iconView?.setImageResource(R.drawable.icon_university)
            "Event" -> holder.iconView?.setImageResource(R.drawable.ic_event)
            "Feature" -> holder.iconView?.setImageResource(R.drawable.ic_feature)
            "Testimonial" -> holder.iconView?.setImageResource(R.drawable.ic_testimonal)
            else -> holder.iconView?.setImageResource(R.drawable.ic_search)
        }
        holder.itemView.setOnClickListener { listener.onItemClick(item) }
    }

    override fun getItemCount(): Int = searchItems.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView? = itemView.findViewById(R.id.result_text)
        val iconView: ImageView? = itemView.findViewById(R.id.result_icon)
    }

    interface OnItemClickListener {
        fun onItemClick(item: SearchItem?)
    }
}