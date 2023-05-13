package com.example.easytodo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.easytodo.R;
import com.example.easytodo.models.Project;

import java.util.List;

public class ProjectsAdapter extends ArrayAdapter<Project> {
    private final List<Project> projectList;

    public ProjectsAdapter(Context context, int textViewResourceId, List<Project> objects) {
        super(context, textViewResourceId);
        projectList = objects;
    }

    @Override
    public void add(@Nullable Project object) {
        projectList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return projectList.size();
    }

    @Nullable
    @Override
    public Project getItem(int position) {
        return projectList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ProjectsAdapter.ViewHolder holder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.project_item, parent, false);
            holder = new ProjectsAdapter.ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ProjectsAdapter.ViewHolder) row.getTag();
        }

        Project project = getItem(position);
        if (project == null) {
            return row;
        }
        holder.titleView.setText(project.getTitle());
        if (project.getDescription().isEmpty()) {
            holder.descriptionView.setVisibility(View.GONE);
        } else {
            holder.descriptionView.setVisibility(View.VISIBLE);
            holder.descriptionView.setText(project.getDescription());
        }
        if (project.getDeadline().isEmpty()) {
            holder.dateTimeView.setVisibility(View.GONE);
        } else {
            holder.dateTimeView.setVisibility(View.VISIBLE);
            holder.dateTimeView.setText(project.getDeadline());
        }

        return row;
    }

    public static class ViewHolder {
        public TextView titleView;
        public TextView descriptionView;
        public TextView dateTimeView;

        public ViewHolder(View view) {
            titleView = view.findViewById(R.id.project_title);
            descriptionView = view.findViewById(R.id.project_description);
            dateTimeView = view.findViewById(R.id.project_date_time);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + titleView.getText() + "'";
        }
    }
}
