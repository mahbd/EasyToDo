package com.example.easytodo.ui.screens;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.easytodo.R;
import com.example.easytodo.adapters.TasksAdapter;
import com.example.easytodo.databinding.FragmentTaskBinding;
import com.example.easytodo.models.DB;
import com.example.easytodo.models.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class TaskScreen extends Fragment {
    private FragmentTaskBinding binding;
    DB.TaskListener taskListener;
    DB.ProjectListener projectListener;
    DB.TagListener tagListener;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<Task> tasks;
        String projectTitle = null, tagTitle = null;
        if (getArguments() != null) {
            projectTitle = getArguments().getString("project");
            tagTitle = getArguments().getString("tag");
        }
        if (projectTitle != null) {
            binding.tasksHeading.setText("Showing tasks for project: " + projectTitle);
            tasks = DB.getTasksByProjectTitle(projectTitle);

        } else if (tagTitle != null) {
            binding.tasksHeading.setText("Showing tasks for tag: " + tagTitle);
            tasks = DB.getTasksByTagTitle(tagTitle);
        } else {
            binding.tasksHeading.setText("Showing all tasks");
            tasks = DB.tasks;
        }

        TasksAdapter adapter = new TasksAdapter(requireContext(), R.layout.task_item, tasks);
        binding.taskList.setAdapter(adapter);

        binding.addNewTask.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_task_form));

        List<Task> finalTasks = tasks;
        binding.taskList.setOnItemLongClickListener((parent, view, position, id) -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.task_popup, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.item_complete) {
                    Task task = finalTasks.get(position);
                    if (task != null) {
                        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("tasks").child(task.id);
                        task.completed = true;
                        childRef.setValue(task);
                    }
                } else if (item.getItemId() == R.id.item_edit) {
                    Task task = finalTasks.get(position);
                    Bundle bundle = new Bundle();
                    if (task != null)
                        bundle.putString("task", task.id);
                    Navigation.findNavController(requireActivity(), R.id.fragment_container)
                            .navigate(R.id.nav_task_form, bundle);
                } else if (item.getItemId() == R.id.item_delete) {
                    Task task = finalTasks.get(position);
                    if (task != null) {
                        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("tasks").child(task.id);
                        childRef.removeValue();
                    }
                }
                return true;
            });
            popupMenu.show();
            return false;
        });

        taskListener = () -> requireActivity().recreate();
        projectListener = () -> requireActivity().recreate();
        tagListener = () -> requireActivity().recreate();
        DB.addTaskListener(taskListener);
        DB.addProjectListener(projectListener);
        DB.addTagListener(tagListener);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        DB.removeTaskListener(taskListener);
        DB.removeProjectListener(projectListener);
        DB.removeTagListener(tagListener);
    }
}