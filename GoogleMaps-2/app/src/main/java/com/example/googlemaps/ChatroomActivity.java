package com.example.googlemaps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemaps.Adapter.ChatMessageRecyclerAdapter;
import com.example.googlemaps.Fragments.UserListFragment;
import com.example.googlemaps.model.ChatRoom;
import com.example.googlemaps.model.Message;
import com.example.googlemaps.model.User;
import com.example.googlemaps.model.UserClient;
import com.example.googlemaps.model.UserLocation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import xyz.hasnat.sweettoast.SweetToast;


public class ChatroomActivity extends AppCompatActivity implements
        View.OnClickListener
{

    private static final String TAG = "ChatroomActivity";
    int abc= 0;
    //widgets
    private ChatRoom mChatroom;
    private EditText mMessage;
    private StorageReference imageMessageStorageRef;
    private String download_url;
    //vars
    private ListenerRegistration mChatMessageEventListener, mUserListEventListener;
    private RecyclerView mChatMessageRecyclerView;
    private ChatMessageRecyclerAdapter mChatMessageRecyclerAdapter;
    private FirebaseFirestore mDb;
    private ArrayList<Message> mMessages = new ArrayList<>();
    private Set<String> mMessageIds = new HashSet<>();
    private ArrayList<User> mUserList = new ArrayList<>();
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    private final static int GALLERY_PICK_CODE = 2;
    private Toolbar toolbars;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        mMessage = findViewById(R.id.input_message);
        mChatMessageRecyclerView = findViewById(R.id.chatmessage_recycler_view);

        findViewById(R.id.checkmark).setOnClickListener(this);
        toolbars = findViewById(R.id.toolbar);
        findViewById(R.id.c_send_image_BTN).setOnClickListener(this);



        mDb = FirebaseFirestore.getInstance();
        imageMessageStorageRef = FirebaseStorage.getInstance().getReference().child("messages_image");

        getIncomingIntent();
        initChatroomRecyclerView();
        getChatroomUsers();

    }

    private void getChatMessages(){

        CollectionReference messagesRef = mDb
                .collection("ChatRooms")
                .document(mChatroom.getChatroom_id())
                .collection("Chat Messages");

        mChatMessageEventListener = messagesRef
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            Toast toast = Toast.makeText(getApplicationContext(),"\"onEvent: Listen failed.\"",Toast.LENGTH_LONG);
                            toast.show();
                            return;

                        }

                        if(queryDocumentSnapshots != null){

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                Message message = doc.toObject(Message.class);
                                if(!mMessageIds.contains(message.getMessage_id())){
                                    mMessageIds.add(message.getMessage_id());
                                    mMessages.add(message);
                                    mChatMessageRecyclerView.smoothScrollToPosition(mMessages.size() - 1);
                                }
                                abc= abc +1;


                            }
                            mChatMessageRecyclerAdapter.notifyDataSetChanged();


                        }
                    }
                });

    }

    private void getChatroomUsers(){

        CollectionReference usersRef = mDb
                .collection("ChatRooms")
                .document(mChatroom.getChatroom_id())
                .collection("User List");

        mUserListEventListener = usersRef
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if(queryDocumentSnapshots != null){

                            // Clear the list and add all the users again
                            mUserList.clear();

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                User user = doc.toObject(User.class);
                                mUserList.add(user);
                                getUserLocation(user);
                            }


                        }
                    }
                });
    }

    private void getUserLocation(User user) {
        DocumentReference locationsRef = mDb
                .collection("User Locations")
                .document(user.getUser_id());

        locationsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    if(task.getResult().toObject(UserLocation.class) != null){
                        UserLocation userLocation=task.getResult().toObject(UserLocation.class);
                        mUserLocations.add(userLocation);
                    }
                }
            }
        });
    }

    private void initChatroomRecyclerView(){
        mChatMessageRecyclerAdapter = new ChatMessageRecyclerAdapter(mMessages, new ArrayList<User>(), this);
        mChatMessageRecyclerView.setAdapter(mChatMessageRecyclerAdapter);
        mChatMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mChatMessageRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mChatMessageRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mMessages.size() > 0){
                                mChatMessageRecyclerView.smoothScrollToPosition(
                                        mChatMessageRecyclerView.getAdapter().getItemCount() - 1);
                            }

                        }
                    }, 100);
                }
            }
        });

    }




    private void clearMessage(){
        mMessage.setText("");
    }

    private void inflateUserListFragment(){
        hideSoftKeyboard();

        UserListFragment fragment = UserListFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("User List", mUserList);
        bundle.putParcelableArrayList("User Locations",mUserLocations);
        bundle.putParcelable("mChatroom",mChatroom);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        transaction.replace(R.id.user_list_container, fragment, "User List");
        transaction.addToBackStack("User List");
        transaction.commit();
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    private void getIncomingIntent(){
        if(getIntent().hasExtra("intent_chatroom")){
            mChatroom = getIntent().getParcelableExtra("intent_chatroom");
            setChatroomName();
            joinChatroom();
        }
    }

    private void leaveChatroom(){

        DocumentReference joinChatroomRef = mDb
                .collection("ChatRooms")
                .document(mChatroom.getChatroom_id())
                .collection("User List")
                .document(FirebaseAuth.getInstance().getUid());

        joinChatroomRef.delete();
        startActivity(new Intent(this, HomeActivity.class));
    }

    private void joinChatroom(){

        DocumentReference joinChatroomRef = mDb
                .collection("ChatRooms")
                .document(mChatroom.getChatroom_id())
                .collection("User List")
                .document(FirebaseAuth.getInstance().getUid());

        User user = ((UserClient)(getApplicationContext())).getUser();
        joinChatroomRef.set(user); // Don't care about listening for completion.
    }

    private void setChatroomName(){

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        getSupportActionBar().setCustomView(toolbars);
        getSupportActionBar().setTitle(mChatroom.getTitle());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChatMessages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mChatMessageEventListener != null){
            mChatMessageEventListener.remove();
        }
        if(mUserListEventListener != null){
            mUserListEventListener.remove();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chatroom, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:{
                UserListFragment fragment =
                        (UserListFragment) getSupportFragmentManager().findFragmentByTag("User List");
                if(fragment != null){
                    if(fragment.isVisible()){
                        getSupportFragmentManager().popBackStack();
                        return true;
                    }
                }
                finish();
                return true;
            }
            case R.id.action_chatroom_user_list:{
                inflateUserListFragment();
                return true;
            }
            case R.id.action_chatroom_leave:{
                leaveChatroom();
                return true;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }

    }
    @Override // for gallery picking
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  For image sending
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri imageUri = data.getData();
            DocumentReference newMessageDoc = mDb
                    .collection("ChatRooms")
                    .document(mChatroom.getChatroom_id())
                    .collection("Chat Messages")
                    .document();

            final String message_push_id = newMessageDoc.getId();

            final StorageReference file_path =imageMessageStorageRef.child(message_push_id + ".jpg");
            UploadTask uploadTask = file_path.putFile(imageUri);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task){
                    if (!task.isSuccessful()){
                        SweetToast.error(ChatroomActivity.this, "Error: " + task.getException().getMessage());
                    }
                    download_url = file_path.getDownloadUrl().toString();
                    return file_path.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        if (task.isSuccessful()){
                            download_url = task.getResult().toString();
                            //Toast.makeText(ChatActivity.this, "From ChatActivity, link: " +download_url, Toast.LENGTH_SHORT).show();

                            Message newChatMessage = new Message();
                            newChatMessage.setMessage(download_url);
                            newChatMessage.setMessage_id(newMessageDoc.getId());

                            User user = ((UserClient)(getApplicationContext())).getUser();
                            Log.d(TAG, "insertNewMessage: retrieved user client: " + user.toString());
                            newChatMessage.setUser(user);
                            newChatMessage.setTimestamp(new Date());
                            newChatMessage.setType("image");

                            newMessageDoc.set(newChatMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        clearMessage();
                                    }else{
                                        View parentLayout = findViewById(android.R.id.content);
                                        Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            Log.e("tag", "Image sent successfully");
                        } else{
                            SweetToast.warning(ChatroomActivity.this, "Failed to send image. Try again");
                        }
                    }
                }
            });
        }
    }
    private void insertNewMessage(){
        String message = mMessage.getText().toString();

        if(!message.equals("")){
            message = message.replaceAll(System.getProperty("line.separator"), "");

            DocumentReference newMessageDoc = mDb
                    .collection("ChatRooms")
                    .document(mChatroom.getChatroom_id())
                    .collection("Chat Messages")
                    .document();

            Message newChatMessage = new Message();
            newChatMessage.setMessage(message);
            newChatMessage.setMessage_id(newMessageDoc.getId());

            User user = ((UserClient)(getApplicationContext())).getUser();
            Log.d(TAG, "insertNewMessage: retrieved user client: " + user.toString());
            newChatMessage.setUser(user);
            newChatMessage.setTimestamp(new Date());
            newChatMessage.setType("text");

            newMessageDoc.set(newChatMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        clearMessage();
                    }else{
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkmark:{
                insertNewMessage();
                break;
            }
            case R.id.c_send_image_BTN:{
                Intent galleryIntent = new Intent().setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK_CODE);
                Log.d(TAG, "onClick: eo co me gi ca");
                break;
            }
        }
    }


}