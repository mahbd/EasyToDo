package com.example.easytodo.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.easytodo.enums.TableEnum;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DB {
    public interface TaskListener {
        void onTaskChanged();
    }

    public interface ProjectListener {
        void onProjectChanged();
    }

    public interface TagListener {
        void onTagChanged();
    }

    public interface ShareListener {
        void onShareChanged();
    }

    public static interface UserListener {
        void onUserChanged();
    }

    public static List<TaskListener> taskListeners = new ArrayList<>();
    public static List<ProjectListener> projectListeners = new ArrayList<>();
    public static List<TagListener> tagListeners = new ArrayList<>();
    public static List<ShareListener> shareListeners = new ArrayList<>();
    public static List<UserListener> userListeners = new ArrayList<>();

    public static void addTaskListener(TaskListener listener) {
        taskListeners.add(listener);
    }

    public static void removeTaskListener(TaskListener listener) {
        taskListeners.remove(listener);
    }

    public static void notifyTaskChanged() {
        for (TaskListener listener : taskListeners) {
            listener.onTaskChanged();
        }
    }

    public static void addProjectListener(ProjectListener listener) {
        projectListeners.add(listener);
    }

    public static void removeProjectListener(ProjectListener listener) {
        projectListeners.remove(listener);
    }

    public static void notifyProjectChanged() {
        for (ProjectListener listener : projectListeners) {
            listener.onProjectChanged();
        }
    }

    public static void addTagListener(TagListener listener) {
        tagListeners.add(listener);
    }

    public static void removeTagListener(TagListener listener) {
        tagListeners.remove(listener);
    }

    public static void notifyTagChanged() {
        for (TagListener listener : tagListeners) {
            listener.onTagChanged();
        }
    }

    public static void addShareListener(ShareListener listener) {
        shareListeners.add(listener);
    }

    public static void removeShareListener(ShareListener listener) {
        shareListeners.remove(listener);
    }

    public static void notifyShareChanged() {
        for (ShareListener listener : shareListeners) {
            listener.onShareChanged();
        }
    }

    public static void addUserListener(UserListener listener) {
        userListeners.add(listener);
    }

    public static void removeUserListener(UserListener listener) {
        userListeners.remove(listener);
    }

    public static void notifyUserChanged() {
        for (UserListener listener : userListeners) {
            listener.onUserChanged();
        }
    }

    public static List<Task> tasks = new ArrayList<>();
    public static List<Project> projects = new ArrayList<>();
    public static List<Tag> tags = new ArrayList<>();
    public static List<Share> shares = new ArrayList<>();
    public static List<User> users = new ArrayList<>();

    static private DatabaseReference tasksRef;
    static private DatabaseReference projectsRef;
    static private DatabaseReference tagsRef;
    static private DatabaseReference sharesRef;
    static private DatabaseReference usersRef;
    static public boolean initialized = false;

    public static void init(GoogleSignInAccount account) {
        String userId = account.getId();
        tasksRef = FirebaseDatabase.getInstance().getReference(userId + "/tasks");
        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tasks.clear();
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task == null) continue;
                    task.id = taskSnapshot.getKey();
                    tasks.add(task);
                }
                notifyTaskChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        projectsRef = FirebaseDatabase.getInstance().getReference(userId + "/projects");
        projectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                projects.clear();
                for (DataSnapshot projectSnapshot : dataSnapshot.getChildren()) {
                    Project project = projectSnapshot.getValue(Project.class);
                    if (project == null) continue;
                    project.id = projectSnapshot.getKey();
                    projects.add(project);
                }
                notifyProjectChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        tagsRef = FirebaseDatabase.getInstance().getReference(userId + "/tags");
        tagsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tags.clear();
                for (DataSnapshot tagSnapshot : dataSnapshot.getChildren()) {
                    Tag tag = tagSnapshot.getValue(Tag.class);
                    if (tag == null) continue;
                    tag.id = tagSnapshot.getKey();
                    tags.add(tag);
                }
                notifyTagChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        sharesRef = FirebaseDatabase.getInstance().getReference(userId + "/shares");
        sharesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shares.clear();
                for (DataSnapshot shareSnapshot : dataSnapshot.getChildren()) {
                    Share share = shareSnapshot.getValue(Share.class);
                    if (share == null) continue;
                    share.id = shareSnapshot.getKey();
                    shares.add(share);
                }
                notifyShareChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user == null) continue;
                    user.id = userSnapshot.getKey();
                    users.add(user);
                }
                notifyUserChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });

        initialized = true;
    }

    public static void addTask(Task task) {
        tasksRef.push().setValue(task);
    }

    public static Task getTask(String id) {
        for (Task task : tasks) {
            if (task.id.equals(id)) {
                return task;
            }
        }
        return null;
    }

    public static List<Task> getTasksByProjectTitle(String projectTitle) {
        List<Task> projectTasks = new ArrayList<>();
        Project project = getProjectByTitle(projectTitle);
        if (project == null) {
            return projectTasks;
        }
        for (Task task : tasks) {
            if (task.project.equals(project.id)) {
                projectTasks.add(task);
            }
        }
        return projectTasks;
    }

    public static List<Task> getTasksByTagTitle(String tagTitle) {
        List<Task> tagTasks = new ArrayList<>();
        Tag tag = getTagByTitle(tagTitle);
        if (tag == null) {
            return tagTasks;
        }
        for (Task task : tasks) {
            for (String tagId : task.tags) {
                if (tagId.equals(tag.id)) {
                    tagTasks.add(task);
                    break;
                }
            }
        }
        return tagTasks;
    }

    public static void updateTask(Task task) {
        tasksRef.child(task.id).setValue(task);
    }

    public static void deleteTask(Task task) {
        tasksRef.child(task.id).removeValue();
    }

    public static List<Task> uncompletedTasks() {
        List<Task> completedTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (!task.completed) {
                completedTasks.add(task);
            }
        }
        return completedTasks;
    }

    public static void addProject(Project project) {
        projectsRef.push().setValue(project);
    }

    public static Project getProject(String id) {
        for (Project project : projects) {
            if (project.id.equals(id)) {
                return project;
            }
        }
        return null;
    }

    public static Project getProjectByTitle(String title) {
        for (Project project : projects) {
            if (project.title.equals(title)) {
                return project;
            }
        }
        return null;
    }

    public static boolean projectExists(String title) {
        return getProjectByTitle(title) != null;
    }

    public static void updateProject(Project project) {
        projectsRef.child(project.id).setValue(project);
    }

    public static void deleteProject(Project project) {
        projectsRef.child(project.id).removeValue();
    }

    public static void addTag(Tag tag) {
        tagsRef.push().setValue(tag);
    }

    public static Tag getTag(String id) {
        for (Tag tag : tags) {
            if (tag.id.equals(id)) {
                return tag;
            }
        }
        return null;
    }

    public static Tag getTagByTitle(String title) {
        for (Tag tag : tags) {
            if (tag.title.equals(title)) {
                return tag;
            }
        }
        return null;
    }

    public static boolean tagExists(String title) {
        return getTagByTitle(title) != null;
    }

    public static void updateTag(Tag tag) {
        tagsRef.child(tag.id).setValue(tag);
    }

    public static void deleteTag(Tag tag) {
        tagsRef.child(tag.id).removeValue();
    }

    public static void addShare(Share share) {
        User toUser = getUserByEmail(share.shared_with);
        sharesRef.push().setValue(share);
        if (toUser != null) {
            DatabaseReference sharesRef2 = FirebaseDatabase.getInstance().getReference(toUser.accountId + "/shares");
            sharesRef2.push().setValue(share);
        }
    }

    public static List<Share> getSharedProjectsByMe(String email) {
        List<Share> sharedProjects = new ArrayList<>();
        for (Share share : shares) {
            if (share.shared_by.equals(email) && share.table.equals(TableEnum.PROJECT.getValue())) {
                sharedProjects.add(share);
            }
        }
        return sharedProjects;
    }

    public static List<Share> getSharedProjectsToMe(String email) {
        List<Share> sharedProjects = new ArrayList<>();
        for (Share share : shares) {
            if (share.shared_with.equals(email) && share.table.equals(TableEnum.PROJECT.getValue())) {
                sharedProjects.add(share);
            }
        }
        return sharedProjects;
    }

    public static List<Share> getSharedTagsByMe(String email) {
        List<Share> sharedTags = new ArrayList<>();
        for (Share share : shares) {
            if (share.shared_by.equals(email) && share.table.equals(TableEnum.TAG.getValue())) {
                sharedTags.add(share);
            }
        }
        return sharedTags;
    }

    public static List<Share> getSharedTagsToMe(String email) {
        List<Share> sharedTags = new ArrayList<>();
        for (Share share : shares) {
            if (share.shared_with.equals(email) && share.table.equals(TableEnum.TAG.getValue())) {
                sharedTags.add(share);
            }
        }
        return sharedTags;
    }

    public static void addUser(GoogleSignInAccount account) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (account == null) return;
        User prevUser = getUserByEmail(account.getEmail());
        User user = new User();
        user.accountId = account.getId();
        user.email = account.getEmail();
        user.name = account.getDisplayName();
        user.picture = account.getPhotoUrl().toString();
        if (prevUser == null) {
            usersRef.push().setValue(user);
        } else {
            user.id = prevUser.id;
            updateUser(user);
        }
    }

    public static User getUserByEmail(String email) {
        for (User user : users) {
            if (user.email.equals(email)) {
                return user;
            }
        }
        return null;
    }

    private static void updateUser(User user) {
        usersRef.child(user.id).setValue(user);
    }
}