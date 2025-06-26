package com.tvm.doctorcube.home.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tvm.doctorcube.R

class SearchResultsAdapter(
    private var items: List<SearchItem>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "SearchResultsAdapter"
    }

    fun updateData(newItems: List<SearchItem>) {
        items = newItems
        notifyDataSetChanged()
        Log.d(TAG, "SearchResultsAdapter updated with ${items.size} items")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_search_result_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { listener.onItemClick(item) }
    }

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView? = itemView.findViewById(R.id.result_text)
        private val iconView: ImageView? = itemView.findViewById(R.id.result_icon)

        init {
            if (textView == null || iconView == null) {
                Log.e(TAG, "Search result item views missing")
            }
        }

        fun bind(item: SearchItem) {
            textView?.text = item.title ?: ""
            iconView?.setImageResource(
                when (item.type) {
                    "University" -> R.drawable.ic_university
                    "Event" -> R.drawable.ic_event
                    "Feature" -> R.drawable.ic_feature
                    "Testimonial" -> R.drawable.ic_testimonal
                    else -> R.drawable.ic_search
                }
            )
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: SearchItem)
    }
}