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
import com.example.easytodo.adapters.ProjectsAdapter;
import com.example.easytodo.databinding.FragmentProjectBinding;
import com.example.easytodo.models.DB;
import com.example.easytodo.models.Project;

import java.util.List;



public class ProjectScreen extends Fragment {
    private FragmentProjectBinding binding;
    DB.ProjectListener projectListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProjectBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<Project> projects = DB.projects;
        ProjectsAdapter adapter = new ProjectsAdapter(getContext(), R.id.project_list, projects);
        binding.projectList.setAdapter(adapter);

        binding.btnNewProject.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_project_form));

        binding.projectList.setOnItemLongClickListener((parent, view, position, id) -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.delete_edit_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.item_edit) {
                    Project project = projects.get(position);
                    Bundle bundle = new Bundle();
                    if (project != null)
                        bundle.putString("project", project.id);
                    Navigation.findNavController(requireActivity(), R.id.fragment_container)
                            .navigate(R.id.nav_project_form, bundle);
                } else if (item.getItemId() == R.id.item_delete) {
                    Project project = projects.get(position);
                    if (project != null) {
                        DB.deleteProject(project);
                    }
                }
                return true;
            });
            popupMenu.show();
            return false;
        });

        binding.projectList.setOnItemClickListener((parent, view, position, id) -> {
            Bundle bundle = new Bundle();
            Project project = projects.get(position);
            if (project != null)
                bundle.putString("project", project.title);
            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                    .navigate(R.id.nav_task, bundle);
        });

        projectListener = () -> requireActivity().recreate();
        DB.addProjectListener(projectListener);

        return root;
    }

    @Override
    public void onDestroyView() {
        DB.removeProjectListener(projectListener);
        super.onDestroyView();
        binding = null;
    }
}