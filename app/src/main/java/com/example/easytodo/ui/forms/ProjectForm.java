package com.example.easytodo.ui.forms;

import static java.lang.String.format;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.easytodo.databinding.FragmentProjectFormBinding;
import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.models.Project;
import com.example.easytodo.utils.Events;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import io.realm.Realm;


public class ProjectForm extends Fragment {
    private FragmentProjectFormBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProjectFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        long projectId;
        Project project = null;
        if (getArguments() != null) {
            projectId = getArguments().getLong("project");
            project = Realm.getDefaultInstance().where(Project.class).equalTo("id", projectId).findFirst();
            if (project != null) {
                binding.etApTitle.setText(project.getTitle());
                binding.etApDescription.setText(project.getDescription());
                OffsetDateTime deadline = project.getDeadline();
                if (deadline != null) {
                    LocalDateTime localDateTime = deadline.toLocalDateTime();
                    binding.etApDate.setText(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    binding.etApTime.setText(localDateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                }
                binding.btnApSave.setText("Update Project");
            }
        }

        binding.etApDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext());
            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                String monthStr = month < 9 ? "0" + (month + 1) : String.valueOf(month + 1);
                String dayStr = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                binding.etApDate.setText(format(Locale.ENGLISH, "%d-%s-%s", year, monthStr, dayStr));
            });
            datePickerDialog.show();
        });

        binding.etApTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                String hourStr = hourOfDay < 10 ? "0" + hourOfDay : String.valueOf(hourOfDay);
                String minuteStr = minute < 10 ? "0" + minute : String.valueOf(minute);
                binding.etApTime.setText(format(Locale.ENGLISH, "%s:%s", hourStr, minuteStr));
            }, 0, 0, true);
            timePickerDialog.show();
        });

        Project finalProject = project;
        binding.btnApSave.setOnClickListener(v -> {
            String deadline;
            String date = binding.etApDate.getText().toString();
            String time = binding.etApTime.getText().toString();
            if (!date.isEmpty() && !time.isEmpty()) {
                deadline = date + "T" + time + ":00+06:00";
            } else if (!date.isEmpty()) {
                deadline = date + "T00:00:00+06:00";
            } else if (!time.isEmpty()) {
                deadline = "1970-01-01T" + time + ":00+06:00";
            } else {
                deadline = "1970-01-01T00:00:00+06:00";
            }
            OffsetDateTime dateTime = OffsetDateTime.parse(deadline);

            String title = binding.etApTitle.getText().toString();
            String description = binding.etApDescription.getText().toString();

            if (title.isEmpty()) {
                binding.etApTitle.setError("Title is required");
                return;
            }
            if ((finalProject == null && Project.exists(title)) || (finalProject != null && Project.exists(title) && !finalProject.getTitle().equals(title))) {
                binding.etApTitle.setError("Title already exists");
                return;
            }
            binding.etApTitle.setError(null);

            if (finalProject != null) {
                Realm.getDefaultInstance().executeTransaction(realm -> {
                    finalProject.setTitle(title);
                    finalProject.setDescription(description);
                    finalProject.setDeadline(dateTime);
                });
                Events.notifyProjectListeners(finalProject.getId(), ActionEnum.UPDATE);
            } else {
                Project newProject = new Project(title, description, dateTime);
                newProject.save();
            }
            requireActivity().onBackPressed();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}