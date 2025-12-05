package com.example.aurora

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView

class MoodUpdate(
    private val context: Context,
    private val moodEntries: List<MoodEntry>
) : BaseAdapter() {

    override fun getCount(): Int = moodEntries.size

    override fun getItem(position: Int): Any = moodEntries[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.mood_list, parent, false)
            holder = ViewHolder(
                view.findViewById(R.id.dateTextView),
                view.findViewById(R.id.moodTextView),
                view.findViewById(R.id.descriptionTextView),
                view.findViewById(R.id.expandLayout)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val entry = moodEntries[position]

        holder.dateTextView.text = entry.date
        holder.moodTextView.text = entry.mood

        // Hide description at first
        holder.descriptionTextView.text = entry.description
        holder.expandLayout.visibility = View.GONE

        view.setOnClickListener {
            holder.expandLayout.visibility =
                if (holder.expandLayout.visibility == View.GONE) View.VISIBLE
                else View.GONE
        }

        return view
    }

    private data class ViewHolder(
        val dateTextView: TextView,
        val moodTextView: TextView,
        val descriptionTextView: TextView,
        val expandLayout: LinearLayout
    )
}
