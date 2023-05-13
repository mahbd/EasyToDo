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

import com.example.easytodo.databinding.FragmentTaskFormBinding;
import com.example.easytodo.models.Task;

import java.time.OffsetDateTime;
import java.util.Locale;


public class TaskForm extends Fragment {
    private FragmentTaskFormBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.etAtDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext());
            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                String monthStr = month < 9 ? "0" + (month + 1) : String.valueOf(month + 1);
                String dayStr = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                binding.etAtDate.setText(format(Locale.ENGLISH, "%d-%s-%s", year, monthStr, dayStr));
            });
            datePickerDialog.show();
        });

        binding.etAtTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                String hourStr = hourOfDay < 10 ? "0" + hourOfDay : String.valueOf(hourOfDay);
                String minuteStr = minute < 10 ? "0" + minute : String.valueOf(minute);
                binding.etAtTime.setText(format(Locale.ENGLISH, "%s:%s", hourStr, minuteStr));
            }, 0, 0, true);
            timePickerDialog.show();
        });

        binding.btnAtSave.setOnClickListener(v -> {
            String deadline;
            String date = binding.etAtDate.getText().toString();
            String time = binding.etAtTime.getText().toString();
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

            String title = binding.etAtTitle.getText().toString();
            String description = binding.etAtDescription.getText().toString();

            if (title.isEmpty()) {
                binding.etAtTitle.setError("Title is required");
                return;
            }
            binding.etAtTitle.setError(null);

            Task task = new Task(title, description, dateTime);
            task.save();
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