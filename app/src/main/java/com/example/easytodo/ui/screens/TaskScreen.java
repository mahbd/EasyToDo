package com.example.easytodo.ui.screens;

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
import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.models.Task;
import com.example.easytodo.utils.Events;

import java.util.List;

import io.realm.Realm;


public class TaskScreen extends Fragment {
    private FragmentTaskBinding binding;
    Events.TaskListener taskListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<Task> tasks = Realm.getDefaultInstance().where(Task.class).equalTo("completed", false).findAll();
        TasksAdapter adapter = new TasksAdapter(requireContext(), R.layout.task_item, tasks);
        binding.taskList.setAdapter(adapter);

        binding.addNewTask.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_task_form));

        binding.taskList.setOnItemLongClickListener((parent, view, position, id) -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.task_popup, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.item_complete) {
                    Task task = tasks.get(position);
                    if (task != null) {
                        Realm.getDefaultInstance().executeTransaction(realm -> {
                            task.setCompleted(true);
                        });
                        requireActivity().recreate();
                        Events.notifyTaskListeners(task.getId(), ActionEnum.UPDATE);
                    }
                } else if (item.getItemId() == R.id.item_edit) {
                    Task task = tasks.get(position);
                    Bundle bundle = new Bundle();
                    if (task != null)
                        bundle.putLong("task", task.getId());
                    Navigation.findNavController(requireActivity(), R.id.fragment_container)
                            .navigate(R.id.nav_task_form, bundle);
                } else if (item.getItemId() == R.id.item_delete) {
                    Task task = tasks.get(position);
                    if (task != null) {
                        Task.delete(task.getId());
                        requireActivity().recreate();
                    }
                }
                return true;
            });
            popupMenu.show();
            return false;
        });

        taskListener = (taskId, action) -> requireActivity().recreate();
        Events.addTaskListener(taskListener);

        return root;
    }

    @Override
    public void onDestroyView() {
        Events.removeTaskListener(taskListener);
        super.onDestroyView();
        binding = null;
    }
}