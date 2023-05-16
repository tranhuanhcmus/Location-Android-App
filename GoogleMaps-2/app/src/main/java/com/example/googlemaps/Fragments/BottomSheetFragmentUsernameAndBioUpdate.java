package com.example.googlemaps.Fragments;


import static com.google.maps.android.Context.getApplicationContext;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.googlemaps.Profile;
import com.example.googlemaps.R;
import com.example.googlemaps.model.User;
import com.example.googlemaps.model.UserClient;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class BottomSheetFragmentUsernameAndBioUpdate extends BottomSheetDialogFragment {
    Context context;
    Boolean isUsername;

    EditText et_user_input_bottom_sheet_fragment;
    TextView btnSave;
    User user;
    TextView btnCancel;
    private FirebaseFirestore mDb;
    String username;
    String bio;

    public BottomSheetFragmentUsernameAndBioUpdate(Context context, Boolean isUsername) {
        this.context = context;
        this.isUsername = isUsername;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bottom_sheet_bio_username_update, container, false);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        view.findViewById(R.id.et_user_input_bottom_sheet_fragment).requestFocus();
        init(view);

        return view;
    }

    private void updateUsernameAndBio() {

        if(isUsername){
            username = et_user_input_bottom_sheet_fragment.getText().toString().trim();
            user.setUsername(username);
            mDb.collection(getString(R.string.collection_users))
                    .document(FirebaseAuth.getInstance().getUid())
                    .set(user);

        }else{
            bio = et_user_input_bottom_sheet_fragment.getText().toString().trim();
            user.setBio(bio);
            mDb.collection(getString(R.string.collection_users))
                    .document(FirebaseAuth.getInstance().getUid())
                    .set(user);

        }
        dismiss();


    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = ((UserClient) getActivity().getApplication()).getUser();
    }

//    private void reloadData() {
//        Intent resultIntent = new Intent();
//        getActivity().setResult(Activity.RESULT_OK, resultIntent);
//        getActivity().finish();
//    }
    private void updateData() {
        // Thực hiện cập nhật dữ liệu

        // Lấy tham chiếu đến Activity thông qua FragmentManager
        Profile activity = (Profile) getActivity();

        // Gọi phương thức của Activity để reload dữ liệu
        if (activity != null) {
            activity.reloadData();
        }
    }
    private void init(View view) {

        mDb = FirebaseFirestore.getInstance();
        et_user_input_bottom_sheet_fragment = view.findViewById(R.id.et_user_input_bottom_sheet_fragment);
        btnSave = view.findViewById(R.id.btn_save_bottom_sheet);
        btnCancel = view.findViewById(R.id.btn_cancel_bottom_sheet);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUsernameAndBio();
                updateData();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }



}
