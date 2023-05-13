package com.example.easytodo.ui.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.easytodo.R;
import com.example.easytodo.adapters.TasksAdapter;
import com.example.easytodo.databinding.FragmentTaskBinding;
import com.example.easytodo.models.Task;

import java.util.List;

import io.realm.Realm;


public class TaskScreen extends Fragment {
    private FragmentTaskBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<Task> taskList = Realm.getDefaultInstance().where(Task.class).findAll();
        TasksAdapter adapter = new TasksAdapter(requireContext(), R.layout.task_item, taskList);
        binding.taskList.setAdapter(adapter);

        binding.addNewTask.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_task_form));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}