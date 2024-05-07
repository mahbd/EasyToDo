package com.example.easytodo.ui.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.easytodo.R;
import com.example.easytodo.models.DB;
import com.example.easytodo.adapters.TasksAdapter;
import com.example.easytodo.databinding.FragmentHomeBinding;
import com.example.easytodo.models.Task;

import java.util.List;


public class HomeScreen extends Fragment {
    private FragmentHomeBinding binding;
    DB.TaskListener taskListener;
    DB.ProjectListener projectListener;
    DB.TagListener tagListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Log.d("HomeScreen", "Rendering");

        List<Task> tasks = DB.completedTasks();
        TasksAdapter adapter = new TasksAdapter(requireContext(), R.layout.task_item, tasks);
        binding.taskList.setAdapter(adapter);
        binding.taskList.setOnItemLongClickListener((parent, view, position, id) -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.task_popup, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.item_complete) {
                    Task task = tasks.get(position);
                    if (task != null) {
                        task.completed = true;
                        DB.updateTask(task);
                    }
                } else if (item.getItemId() == R.id.item_edit) {
                    Task task = tasks.get(position);
                    Bundle bundle = new Bundle();
                    if (task != null)
                        bundle.putString("task", task.id);
                    Navigation.findNavController(requireActivity(), R.id.fragment_container)
                            .navigate(R.id.nav_task_form, bundle);
                } else if (item.getItemId() == R.id.item_delete) {
                    Task task = tasks.get(position);
                    if (task != null) {
                        DB.deleteTask(task);
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