package com.example.easytodo.ui.forms;

import static java.lang.String.format;

import android.annotation.SuppressLint;
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
import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.models.Project;
import com.example.easytodo.models.Tag;
import com.example.easytodo.models.Task;
import com.example.easytodo.utils.DateTimeExtractor;
import com.example.easytodo.utils.Events;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;


public class TaskForm extends Fragment {
    private FragmentTaskFormBinding binding;
    private String EMPTY = "None";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RealmResults<Project> projects = Realm.getDefaultInstance().where(Project.class).findAll();
        List<String> projectTitles = new ArrayList<>();
        projectTitles.add(EMPTY);
        for (Project project : projects) {
            projectTitles.add(project.getTitle());
        }
        RealmResults<Tag> tags = Realm.getDefaultInstance().where(Tag.class).findAll();
        List<String> tagTitles = new ArrayList<>();
        tagTitles.add(EMPTY);
        for (Tag tag : tags) {
            tagTitles.add(tag.getTitle());
        }

        long taskId;
        Task task = null;

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
                        binding.etAtDate.setText(data.date.toString());
                        binding.etAtTime.setText(data.time.toString());
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
            taskId = getArguments().getLong("task");
            task = Realm.getDefaultInstance().where(Task.class).equalTo("id", taskId).findFirst();
            if (task != null) {
                binding.etAtTitle.setText(task.getTitle());
                binding.etAtDescription.setText(task.getDescription());
                OffsetDateTime deadline = task.getDeadline();
                if (deadline != null) {
                    LocalDateTime localDateTime = deadline.toLocalDateTime();
                    binding.etAtDate.setText(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    binding.etAtTime.setText(localDateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                }
                if (!task.getProject_title().isEmpty() && projectTitles.contains(task.getProject_title())) {
                    binding.spAtProject.setSelection(projectTitles.indexOf(task.getProject_title()));
                }
                if (!task.getTagsString().isEmpty() && tagTitles.contains(task.getTagsString())) {
                    binding.spAtTags.setSelection(tagTitles.indexOf(task.getTagsString()));
                }
                binding.btnAtSave.setText("Update Task");
            }
        }

        Task finalTask = task;
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

            if (finalTask != null) {
                Realm.getDefaultInstance().executeTransaction(realm -> {
                    finalTask.setTitle(title);
                    finalTask.setDescription(description);
                    finalTask.setDeadline(dateTime);
                });
                Events.notifyTaskListeners(finalTask.getId(), ActionEnum.UPDATE);
            } else {
                Task newTask = new Task(title, description, dateTime, project, tag);
                newTask.save();
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