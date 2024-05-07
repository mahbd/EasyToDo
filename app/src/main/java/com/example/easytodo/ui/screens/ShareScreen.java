package com.example.easytodo.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.easytodo.LoginActivity;
import com.example.easytodo.R;
import com.example.easytodo.databinding.FragmentShareBinding;
import com.example.easytodo.models.DB;
import com.example.easytodo.models.Share;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ShareScreen extends Fragment {
    private FragmentShareBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShareBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (account == null) {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            return root;
        }


        List<Share> shares = DB.getSharedProjectsByMe(account.getEmail());
        List<Map<String, String>> psbData = new ArrayList<>();
        for (Share share : shares) {
            Map<String, String> data = Map.of("title", share.title, "username", share.shared_with);
            psbData.add(data);
        }
        SimpleAdapter adapter = getAdapter(psbData);
        binding.projectsSharedByMe.setAdapter(adapter);


        List<Share> shares2 = DB.getSharedProjectsToMe(account.getEmail());
        List<Map<String, String>> psbData2 = new ArrayList<>();
        for (Share share : shares2) {
            Map<String, String> data = Map.of("title", share.title, "username", share.shared_by);
            psbData2.add(data);
        }
        SimpleAdapter adapter2 = getAdapter(psbData2);
        binding.projectsSharedWithMe.setAdapter(adapter2);

        List<Share> shares3 = DB.getSharedTagsByMe(account.getEmail());
        List<Map<String, String>> psbData3 = new ArrayList<>();
        for (Share share : shares3) {
            Map<String, String> data = Map.of("title", share.title, "username", share.shared_with);
            psbData3.add(data);
        }
        SimpleAdapter adapter3 = getAdapter(psbData3);
        binding.tagsSharedByMe.setAdapter(adapter3);

        List<Share> shares4 = DB.getSharedTagsToMe(account.getEmail());
        List<Map<String, String>> psbData4 = new ArrayList<>();
        for (Share share : shares4) {
            Map<String, String> data = Map.of("title", share.title, "username", share.shared_by);
            psbData4.add(data);
        }
        SimpleAdapter adapter4 = getAdapter(psbData4);
        binding.tagsSharedWithMe.setAdapter(adapter4);

        // TODO: get tasks shared with me
//        TaskAPI taskAPI = GenAPIS.getAPI(TaskAPI.class);
//        H.enqueueReq(taskAPI.getTasksWithMe(), (call, response) -> {
//            if (response.isSuccessful() && response.body() != null) {
//                List<Task> tasks = response.body();
//                TasksAdapter adapter = new TasksAdapter(requireContext(), R.layout.task_item, tasks);
//                binding.taskList.setAdapter(adapter);
//            }
//        });

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