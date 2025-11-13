package com.example.aurora;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.LinearLayout;

import java.util.List;

public class MoodUpdate extends BaseAdapter {

    private Context context;
    private List<MoodEntry> moodEntries;

    // Constructor
    public MoodUpdate(Context context, List<MoodEntry> moodEntries) {
        this.context = context;
        this.moodEntries = moodEntries;
    }

    @Override
    public int getCount() {
        return moodEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return moodEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.mood_list, parent, false);
            holder = new ViewHolder();
            holder.dateTextView = convertView.findViewById(R.id.dateTextView);
            holder.moodTextView = convertView.findViewById(R.id.moodTextView);
            holder.descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
            holder.expandLayout = convertView.findViewById(R.id.expandLayout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MoodEntry entry = moodEntries.get(position);
        holder.dateTextView.setText(entry.getDate());
        holder.moodTextView.setText(entry.getMood());

        // Initially hide the description
        holder.descriptionTextView.setText(entry.getDescription());
        holder.expandLayout.setVisibility(View.GONE);

        // Set up the click listener to toggle visibility of the description
        convertView.setOnClickListener(v -> {
            if (holder.expandLayout.getVisibility() == View.GONE) {
                holder.expandLayout.setVisibility(View.VISIBLE);
            } else {
                holder.expandLayout.setVisibility(View.GONE);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView dateTextView;
        TextView moodTextView;
        TextView descriptionTextView;
        LinearLayout expandLayout;
    }
}
