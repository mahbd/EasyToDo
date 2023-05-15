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
import com.example.easytodo.models.Project;

import java.util.List;

import io.realm.Realm;


public class ProjectScreen extends Fragment {
    private FragmentProjectBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProjectBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<Project> projects = Realm.getDefaultInstance().where(Project.class).findAll();
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
                        bundle.putLong("project", project.getId());
                    Navigation.findNavController(requireActivity(), R.id.fragment_container)
                            .navigate(R.id.nav_project_form, bundle);
                } else if (item.getItemId() == R.id.item_delete) {
                    Project project = projects.get(position);
                    if (project != null) {
                        Project.delete(project.getId());
                        requireActivity().recreate();
                    }
                }
                return true;
            });
            popupMenu.show();
            return false;
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}