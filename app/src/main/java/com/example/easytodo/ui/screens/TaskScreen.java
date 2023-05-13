package com.example.easytodo.ui.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.easytodo.R;
import com.example.easytodo.adapters.TasksAdapter;
import com.example.easytodo.databinding.FragmentTaskBinding;
import com.example.easytodo.models.Task;

import java.util.ArrayList;
import java.util.List;


public class TaskScreen extends Fragment {
    private FragmentTaskBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Task task1 = new Task("Task 1", "This is task 1", "", 0, false, 0, 1, new ArrayList<>(), 0, "", 0);
        TasksAdapter adapter = new TasksAdapter(requireContext(), R.layout.task_item, List.of(task1));
        binding.taskList.setAdapter(adapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}