package com.example.easytodo.ui.screens;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.example.easytodo.R;
import com.example.easytodo.adapters.TasksAdapter;
import com.example.easytodo.databinding.FragmentShareBinding;
import com.example.easytodo.enums.TableEnum;
import com.example.easytodo.models.Share;
import com.example.easytodo.models.Task;
import com.example.easytodo.models.User;
import com.example.easytodo.services.GenAPIS;
import com.example.easytodo.services.ShareAPI;
import com.example.easytodo.services.TaskAPI;
import com.example.easytodo.utils.H;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;


public class ShareScreen extends Fragment {
    private FragmentShareBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShareBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        String USER = prefs.getString("username", "mah");

        ShareAPI shareAPI = GenAPIS.getAPI(ShareAPI.class);
        Map<String, String> query = Map.of("user__username", USER, "table", TableEnum.PROJECT.getValue());
        H.enqueueReq(shareAPI.getShares(query), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                List<Share> shares = response.body();
                List<Map<String, String>> psbData = new ArrayList<>();
                for (Share share : shares) {
                    User user = Realm.getDefaultInstance().where(User.class).equalTo("id", share.getShared_with()).findFirst();
                    Map<String, String> data = Map.of("title", share.getTitle(), "username", user != null ? user.getUsername(): Long.toString(share.getShared_with()));
                    psbData.add(data);
                }
                SimpleAdapter adapter = getAdapter(psbData);
                binding.projectsSharedByMe.setAdapter(adapter);
            }
        });

        query = Map.of("shared_with__username", USER, "table", TableEnum.PROJECT.getValue());
        H.enqueueReq(shareAPI.getShares(query), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                List<Share> shares = response.body();
                List<Map<String, String>> psbData = new ArrayList<>();
                for (Share share : shares) {
                    User user = Realm.getDefaultInstance().where(User.class).equalTo("id", share.getUser()).findFirst();
                    Map<String, String> data = Map.of("title", share.getTitle(), "username", user != null ? user.getUsername(): Long.toString(share.getUser()));
                    psbData.add(data);
                }
                SimpleAdapter adapter = getAdapter(psbData);
                binding.projectsSharedWithMe.setAdapter(adapter);
            }
        });

        query = Map.of("user__username", USER, "table", TableEnum.TAG.getValue());
        H.enqueueReq(shareAPI.getShares(query), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                List<Share> shares = response.body();
                List<Map<String, String>> psbData = new ArrayList<>();
                for (Share share : shares) {
                    User user = Realm.getDefaultInstance().where(User.class).equalTo("id", share.getShared_with()).findFirst();
                    Map<String, String> data = Map.of("title", share.getTitle(), "username", user != null ? user.getUsername(): Long.toString(share.getShared_with()));
                    psbData.add(data);
                }
                SimpleAdapter adapter = getAdapter(psbData);
                binding.tagsSharedByMe.setAdapter(adapter);
            }
        });

        query = Map.of("shared_with__username", USER, "table", TableEnum.TAG.getValue());
        H.enqueueReq(shareAPI.getShares(query), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                List<Share> shares = response.body();
                List<Map<String, String>> psbData = new ArrayList<>();
                for (Share share : shares) {
                    User user = Realm.getDefaultInstance().where(User.class).equalTo("id", share.getUser()).findFirst();
                    Map<String, String> data = Map.of("title", share.getTitle(), "username", user != null ? user.getUsername(): Long.toString(share.getUser()));
                    psbData.add(data);
                }
                SimpleAdapter adapter = getAdapter(psbData);
                binding.tagsSharedWithMe.setAdapter(adapter);
            }
        });

        TaskAPI taskAPI = GenAPIS.getAPI(TaskAPI.class);
        H.enqueueReq(taskAPI.getTasksWithMe(), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                List<Task> tasks = response.body();
                TasksAdapter adapter = new TasksAdapter(requireContext(), R.layout.task_item, tasks);
                binding.taskList.setAdapter(adapter);
            }
        });

        binding.shareMore.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_share_form));

        return root;
    }

    private SimpleAdapter getAdapter(List<Map<String, String>> data) {
        String[] from = {"title", "username"};
        int[] to = {android.R.id.text1, android.R.id.text2};
        return new SimpleAdapter(getContext(), data, android.R.layout.simple_list_item_2, from, to);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}