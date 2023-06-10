package com.example.easytodo.ui.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.easytodo.R;
import com.example.easytodo.databinding.FragmentTagBinding;
import com.example.easytodo.models.Sync;
import com.example.easytodo.models.Tag;
import com.example.easytodo.utils.Events;

import io.realm.Realm;
import io.realm.RealmResults;


public class TagScreen extends Fragment {
    private FragmentTagBinding binding;
    Events.TagListener tagListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTagBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RealmResults<Tag> tags = Realm.getDefaultInstance().where(Tag.class).findAll();
        String[] tagNames = new String[tags.size()];
        for (Tag tag : tags) {
            tagNames[tags.indexOf(tag)] = tag.getTitle();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, tagNames);
        binding.tagList.setAdapter(adapter);

        binding.btnNewTag.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_tag_form));

        binding.tagList.setOnItemLongClickListener((parent, view, position, id) -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.delete_edit_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.item_edit) {
                    Tag tag = tags.get(position);
                    Bundle bundle = new Bundle();
                    if (tag != null)
                        bundle.putLong("tag", tag.getId());
                    Navigation.findNavController(requireActivity(), R.id.fragment_container)
                            .navigate(R.id.nav_tag_form, bundle);
                } else if (item.getItemId() == R.id.item_delete) {
                    Tag tag = tags.get(position);
                    if (tag != null) {
                        Tag.delete(tag.getId());
                    }
                }
                return true;
            });
            popupMenu.show();
            return false;
        });

        binding.tagList.setOnItemClickListener((parent, view, position, id) -> {
            Tag tag = tags.get(position);
            if (tag != null) {
                Bundle bundle = new Bundle();
                bundle.putString("tag", tag.getTitle());
                Navigation.findNavController(requireActivity(), R.id.fragment_container)
                        .navigate(R.id.nav_task, bundle);
            }
        });

        tagListener = (tagId, action) -> requireActivity().recreate();
        Events.addTagListener(tagListener);

        return root;
    }

    @Override
    public void onDestroyView() {
        Events.removeTagListener(tagListener);
        super.onDestroyView();
        binding = null;
    }
}