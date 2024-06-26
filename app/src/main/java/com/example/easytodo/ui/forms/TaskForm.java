package com.example.easytodo.ui.forms;

import static java.lang.String.format;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.easytodo.databinding.FragmentTaskFormBinding;
import com.example.easytodo.models.DB;
import com.example.easytodo.models.Project;
import com.example.easytodo.models.Tag;
import com.example.easytodo.models.Task;
import com.example.easytodo.utils.DateTimeExtractor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class TaskForm extends Fragment {
    private FragmentTaskFormBinding binding;
    private final String EMPTY = "None";
    DB.TaskListener taskListener;
    DB.ProjectListener projectListener;
    DB.TagListener tagListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<Project> projects = DB.projects;
        List<Tag> tags = DB.tags;

        List<String> projectTitles = new ArrayList<>();
        projectTitles.add(EMPTY);
        for (Project project : projects) {
            projectTitles.add(project.title);
        }
        List<String> tagTitles = new ArrayList<>();
        tagTitles.add(EMPTY);
        for (Tag tag : tags) {
            tagTitles.add(tag.title);
        }

        String taskId;

        binding.etAtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int currentSelection = binding.etAtTitle.getSelectionStart();
                binding.etAtTitle.removeTextChangedListener(this);
                boolean detected = false;
                if (s.length() > 0) {
                    SpannableStringBuilder builder = new SpannableStringBuilder(s);
                    DateTimeExtractor.ExtractedData data = DateTimeExtractor.after_matcher(s.toString());
                    if (!data.found) {
                        DateTimeExtractor.ExtractedData data_date, data_time;
                        data_date = DateTimeExtractor.full_date_matcher(s.toString());
                        if (!data_date.found) {
                            data_date = DateTimeExtractor.half_date_matcher(s.toString());
                        }
                        if (data_date.found) {
                            detected = true;
                            binding.etAtDate.setText(data_date.date);
                            int start = data_date.start;
                            int end = data_date.end;
                            builder.setSpan(new android.text.style.ForegroundColorSpan(Color.RED), start, end, 0);
                        }
                        data_time = DateTimeExtractor.full_time_matcher(s.toString());
                        if (!data_time.found) {
                            data_time = DateTimeExtractor.half_time_matcher(s.toString());
                        }
                        if (data_time.found) {
                            detected = true;
                            binding.etAtTime.setText(data_time.time);
                            int start = data_time.start;
                            int end = data_time.end;
                            builder.setSpan(new android.text.style.ForegroundColorSpan(Color.RED), start, end, 0);
                        }
                        if (data_time.found && !data_date.found) {
                            binding.etAtDate.setText(data_time.date);
                        }
                    } else {
                        detected = true;
                        binding.etAtDate.setText(data.date);
                        binding.etAtTime.setText(data.time);
                        int start = data.start;
                        int end = data.end;
                        builder.setSpan(new android.text.style.ForegroundColorSpan(Color.RED), start, end, 0);
                    }
                    if (!detected) {
                        String text = builder.toString();
                        binding.etAtTitle.setText(text);
                    } else {
                        binding.etAtTitle.setText(builder);
                    }
                    binding.etAtTitle.setSelection(currentSelection);
                }
                binding.etAtTitle.addTextChangedListener(this);
            }
        });

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

        ArrayAdapter<String> projectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, projectTitles);
        binding.spAtProject.setAdapter(projectAdapter);
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, tagTitles);
        binding.spAtTags.setAdapter(tagAdapter);


        if (getArguments() != null) {
            taskId = getArguments().getString("task");
            Task task = DB.getTask(taskId);
            if (task != null) {
                Project project = DB.getProject(task.project);
                StringBuilder taskTagsStringBuilder = new StringBuilder();
                for (String tagId : task.tags) {
                    Tag tag = DB.getTag(tagId);
                    if (tag != null) {
                        taskTagsStringBuilder.append(tag.title).append(", ");
                    }
                }
                String taskTags = taskTagsStringBuilder.toString();
                if (project != null) {
                    binding.etAtTitle.setText(task.title);
                    binding.etAtDescription.setText(task.description);
                    OffsetDateTime deadline = OffsetDateTime.parse(task.deadline);
                    if (deadline != null) {
                        LocalDateTime localDateTime = deadline.toLocalDateTime();
                        binding.etAtDate.setText(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        binding.etAtTime.setText(localDateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                    }
                    if (!project.title.isEmpty() && projectTitles.contains(project.title)) {
                        binding.spAtProject.setSelection(projectTitles.indexOf(project.title));
                    }
                    if (!taskTags.isEmpty() && tagTitles.contains(taskTags)) {
                        binding.spAtTags.setSelection(tagTitles.indexOf(taskTags));
                    }
                    binding.btnAtSave.setText("Update Task");
                }

            }
        } else {
            taskId = null;
        }

        binding.btnAtSave.setOnClickListener(v -> {
            String deadline;
            String date = binding.etAtDate.getText().toString();
            String time = binding.etAtTime.getText().toString();
            String project = binding.spAtProject.getSelectedItem().toString();
            if (project.equals(EMPTY)) {
                project = "";
            }
            String tag = binding.spAtTags.getSelectedItem().toString();
            if (tag.equals(EMPTY)) {
                tag = "";
            }
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

            if (taskId != null) {
                Task task = DB.getTask(taskId);
                assert task != null;
                task.title = title;
                task.description = description;
                task.deadline = dateTime.toString();
                DB.updateTask(task);
            } else {
                Task newTask = new Task();
                newTask.title = title;
                newTask.description = description;
                newTask.deadline = deadline.toString();
                if (!project.isEmpty()) {
                    Project selectedProject = DB.getProjectByTitle(project);
                    if (selectedProject != null)
                        newTask.project = selectedProject.id;
                }
                if (!tag.isEmpty()) {
                    Tag selectedTag = DB.getTagByTitle(tag);
                    if (selectedTag != null) {
                        newTask.tags = new ArrayList<>();
                        newTask.tags.add(selectedTag.id);
                    }
                }
                DB.addTask(newTask);
            }
            requireActivity().onBackPressed();
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