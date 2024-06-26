package com.example.easytodo.ui.forms;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.easytodo.LoginActivity;
import com.example.easytodo.databinding.FragmentShareFormBinding;
import com.example.easytodo.enums.TableEnum;
import com.example.easytodo.models.DB;
import com.example.easytodo.models.Project;
import com.example.easytodo.models.Share;
import com.example.easytodo.models.Tag;
import com.example.easytodo.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.List;


public class ShareForm extends Fragment {
    private FragmentShareFormBinding binding;
    DB.ProjectListener projectListener;
    DB.TagListener tagListener;
    DB.UserListener userListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShareFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (account == null) {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            return root;
        }

        List<String> userEmails = new ArrayList<>();
        for (User user : DB.users) {
            userEmails.add(user.email);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, userEmails);
        binding.spinnerUserEmail.setAdapter(adapter);

        List<String> itemTypes = new ArrayList<>();
        itemTypes.add(TableEnum.TAG.getValue());
        itemTypes.add(TableEnum.PROJECT.getValue());
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, itemTypes);
        binding.spinnerType.setAdapter(typeAdapter);

        List<String> tags = new ArrayList<>();
        for (Tag tag : DB.tags) {
            tags.add(tag.title);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, tags);
        binding.spinnerItem.setAdapter(dataAdapter);

        binding.spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataAdapter.clear();
                if (position == 0) {
                    for (Tag tag : DB.tags) {
                        dataAdapter.add(tag.title);
                    }
                } else {
                    for (Project project : DB.projects) {
                        dataAdapter.add(project.title);
                    }
                }
                dataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnShare.setOnClickListener(v -> {
            String userEmail = binding.spinnerUserEmail.getSelectedItem().toString();
            String itemType = binding.spinnerType.getSelectedItem().toString();
            String item = binding.spinnerItem.getSelectedItem().toString();

            String itemId;
            if (itemType.equals(TableEnum.TAG.getValue())) {
                Tag tag = DB.getTagByTitle(item);
                if (tag == null) {
                    Toast.makeText(getContext(), "Invalid tag", Toast.LENGTH_SHORT).show();
                    return;
                }
                itemId = tag.id;
            } else {
                Project project = DB.getProjectByTitle(item);
                if (project == null) {
                    Toast.makeText(getContext(), "Invalid project", Toast.LENGTH_SHORT).show();
                    return;
                }
                itemId = project.id;
            }

            if (userEmail.isEmpty()) {
                Toast.makeText(getContext(), "Invalid user or item", Toast.LENGTH_SHORT).show();
                return;
            }

            Share newShare = new Share();
            newShare.title = item;
            newShare.shared_with = userEmail;
            newShare.table = itemType;
            newShare.data_id = itemId;
            newShare.shared_by = account.getEmail();

            DB.addShare(newShare);
            Toast.makeText(getContext(), "Item shared", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        });

        projectListener = () -> requireActivity().recreate();

        tagListener = () -> requireActivity().recreate();

        userListener = () -> requireActivity().recreate();

        DB.addProjectListener(projectListener);
        DB.addTagListener(tagListener);
        DB.addUserListener(userListener);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DB.removeProjectListener(projectListener);
        DB.removeTagListener(tagListener);
        DB.removeUserListener(userListener);
        binding = null;
    }
}