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
import com.example.easytodo.models.Project;

import java.time.LocalDateTime;
import java.util.Locale;


public class ProjectForm extends Fragment {
    private FragmentProjectFormBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProjectFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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

        binding.btnApSave.setOnClickListener(v -> {
            String deadline;
            String date = binding.etApDate.getText().toString();
            String time = binding.etApTime.getText().toString();
            if (!date.isEmpty() && !time.isEmpty()) {
                deadline = date + "T" + time + ":00";
            } else if (!date.isEmpty()) {
                deadline = date + "T00:00:00";
            } else if (!time.isEmpty()) {
                deadline = "1970-01-01T" + time + ":00";
            } else {
                deadline = "1970-01-01T00:00:00";
            }
            LocalDateTime dateTime = LocalDateTime.parse(deadline);

            String title = binding.etApTitle.getText().toString();
            String description = binding.etApDescription.getText().toString();

            if (title.isEmpty()) {
                binding.etApTitle.setError("Title is required");
                return;
            }
            if (Project.exists(title)) {
                binding.etApTitle.setError("Title already exists");
                return;
            }
            binding.etApTitle.setError(null);

            Project project = new Project(title, description, dateTime);
            project.save();
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