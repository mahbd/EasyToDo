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
import com.example.easytodo.models.DB;
import com.example.easytodo.models.Project;
import com.example.easytodo.models.Tag;
import com.example.easytodo.models.Task;

import java.util.List;

public class TasksAdapter extends ArrayAdapter<Task> {

    private final List<Task> taskList;

    public TasksAdapter(Context context, int textViewResourceId, List<Task> objects) {
        super(context, textViewResourceId);
        taskList = objects;
    }

    @Override
    public void add(@Nullable Task object) {
        taskList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Nullable
    @Override
    public Task getItem(int position) {
        return taskList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.task_item, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Task task = getItem(position);
        if (task == null) {
            return row;
        }
        Project project = DB.getProject(task.project);
        StringBuilder tagsStrBuilder = new StringBuilder();
        if (task.tags == null) {
            task.tags = List.of();
        }
        for (String tagId : task.tags) {
            Tag tag = DB.getTag(tagId);
            if (tag != null) {
                tagsStrBuilder.append(tag.title).append(", ");
            }
        }
        String tagsStr = tagsStrBuilder.toString();

        holder.titleView.setText(task.title);
        if (task.description.isEmpty()) {
            holder.descriptionView.setVisibility(View.GONE);
        } else {
            holder.descriptionView.setVisibility(View.VISIBLE);
            holder.descriptionView.setText(task.description);
        }
        if (task.getDeadlineStr().isEmpty()) {
            holder.dateTimeView.setVisibility(View.GONE);
        } else {
            holder.dateTimeView.setVisibility(View.VISIBLE);
            holder.dateTimeView.setText(task.getDeadlineStr());
        }
        if (task.reminder == 0) {
            holder.alarmView.setVisibility(View.GONE);
        } else {
            holder.alarmView.setVisibility(View.VISIBLE);
            holder.alarmView.setText(task.reminder);
        }
        if (project == null || project.title.isEmpty()) {
            holder.projectView.setVisibility(View.GONE);
        } else {
            holder.projectView.setVisibility(View.VISIBLE);
            holder.projectView.setText(project.title);
        }
        if (tagsStr.isEmpty()) {
            holder.tagView.setVisibility(View.GONE);
        } else {
            holder.tagView.setVisibility(View.VISIBLE);
            holder.tagView.setText(tagsStr);
        }

        return row;
    }

    public static class ViewHolder {
        public TextView titleView;
        public TextView descriptionView;
        public TextView dateTimeView;
        public TextView alarmView;
        public TextView projectView;
        public TextView tagView;

        public ViewHolder(View view) {
            titleView = view.findViewById(R.id.task_title);
            descriptionView = view.findViewById(R.id.task_description);
            dateTimeView = view.findViewById(R.id.task_date_time);
            alarmView = view.findViewById(R.id.task_alarm_date_time);
            projectView = view.findViewById(R.id.task_project);
            tagView = view.findViewById(R.id.task_tag);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + titleView.getText() + "'";
        }
    }
}
