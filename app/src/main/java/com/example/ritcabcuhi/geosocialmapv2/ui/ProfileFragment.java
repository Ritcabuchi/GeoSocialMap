package com.example.ritcabcuhi.geosocialmapv2.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ritcabcuhi.geosocialmapv2.R;
import com.example.ritcabcuhi.geosocialmapv2.api.UserApi;
import com.example.ritcabcuhi.geosocialmapv2.api.ApiListener;
import com.example.ritcabcuhi.geosocialmapv2.eventbus.DataEditEvent;
import com.example.ritcabcuhi.geosocialmapv2.manager.CurrentUser;
import com.example.ritcabcuhi.geosocialmapv2.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final int RESULT_LOAD_IMAGE = 1;

    @BindView(R.id.imageProfile)
    CircleImageView imageProfile;
    @BindView(R.id.textUserName)
    TextView textUserName;
    @BindView(R.id.btnEdit)
    FloatingActionButton btnEdit;
    @BindView(R.id.edtPosition)
    EditText edtPosition;
    @BindView(R.id.edtWorkPlace)
    EditText edtWorkPlace;
    @BindView(R.id.edtAddress)
    EditText edtAddress;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPhoneNum)
    EditText edtPhoneNum;

    private boolean editable = false;
    private boolean edited = false;

    private Uri profileImageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this,view);

        setupEvent();
        updateView();
        return view;
    }

    private void setupEvent(){
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != CurrentUser.getInstace().getUser().getEmail())
                    edited = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != CurrentUser.getInstace().getUser().getAddress())
                    edited = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtWorkPlace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != CurrentUser.getInstace().getUser().getWorkingPlace())
                    edited = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtPosition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != CurrentUser.getInstace().getUser().getPosition())
                    edited = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence != CurrentUser.getInstace().getUser().getPhoneNumber())
                    edited = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void updateView(){
        bindView();

        if(editable){
            enableEdit();
            btnEdit.setBackgroundColor(getActivity().getResources().getColor(R.color.grey));
        }
        else{
            disableEdit();
            btnEdit.setBackgroundColor(getActivity().getResources().getColor(R.color.colorWhite));
        }
    }

    private User createUserData(){
        User user = new User();

        user.setId(CurrentUser.getInstace().getUser().getId());
        user.setName(textUserName.getText().toString());
        user.setEmail(edtEmail.getText().toString());
        user.setAddress(edtAddress.getText().toString());
        user.setPhoneNumber(edtPhoneNum.getText().toString());
        user.setPosition(edtPosition.getText().toString());
        user.setWorkingPlace(edtWorkPlace.getText().toString());

        user.setImageUrl(generateImageUrl());

        return user;
    }

    String generateImageUrl(){
        return "profileImage/"+ CurrentUser.getInstace().getUser().getId() + ".jpg";
    }

    void showDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.layout_did_not_save_dialog, null);
        dialog.setView(view);

        dialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final ProgressDialog mProgress = new ProgressDialog(getContext());
                mProgress.show();

                UserApi.getInstance().updateUserData(createUserData()).setListener(new ApiListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        mProgress.dismiss();
                        edited = false;
                        uploadImage();
                    }

                    @Override
                    public void onFailure(DatabaseError error) {
                        mProgress.dismiss();
                    }
                });

                toggleEditable();
            }
        });

        dialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                toggleEditable();
                updateView();
            }
        });

        dialog.show();
    }

    @Subscribe
    void onEditData(DataEditEvent e){
        updateView();
    }

    @OnClick(R.id.btnEdit)
    void handleEditButton(){
        toggleEditable();
        if(editable && edited){
            showDialog();
        }

    }

    void toggleEditable(){
        editable = !editable;
        if(editable){
            enableEdit();
            btnEdit.setBackgroundColor(getActivity().getResources().getColor(R.color.grey));
        }
        else{
            disableEdit();
            btnEdit.setBackgroundColor(getActivity().getResources().getColor(R.color.colorWhite));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void bindView(){
        User user = CurrentUser.getInstace().getUser();

        if(user.getImageUri()!=null){
            Glide.with(getActivity()).load(user.getImageUri()).into(imageProfile);
        }
        if(user.getName()!=null)
            textUserName.setText(user.getName());
        if(user.getPosition() != null)
            edtPosition.setText(user.getPosition());
        if(user.getWorkingPlace() != null)
            edtWorkPlace.setText(user.getWorkingPlace());
        if(user.getAddress() != null)
            edtAddress.setText(user.getAddress());
        if(user.getEmail() != null)
            edtEmail.setText(user.getEmail());
        if(user.getPhoneNumber() != null)
            edtPhoneNum.setText(user.getPhoneNumber());
    }

    private void disableEdit(){
        edtPosition.setInputType(InputType.TYPE_NULL);
        edtWorkPlace.setInputType(InputType.TYPE_NULL);
        edtAddress.setInputType(InputType.TYPE_NULL);
        edtEmail.setInputType(InputType.TYPE_NULL);
        edtPhoneNum.setInputType(InputType.TYPE_NULL);
    }

    private void enableEdit(){
        edtPosition.setInputType(InputType.TYPE_CLASS_TEXT);
        edtWorkPlace.setInputType(InputType.TYPE_CLASS_TEXT);
        edtAddress.setInputType(InputType.TYPE_CLASS_TEXT);
        edtEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        edtPhoneNum.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    private void uploadImage() {
        Log.d(TAG, "uploadImage: storage upload : " + generateImageUrl());

        StorageReference storage = FirebaseStorage.getInstance().getReference();

        if(profileImageUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("กำลังอัพโหลด...");
            progressDialog.show();

            StorageReference ref = storage.child(generateImageUrl());
            ref.putFile(profileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            CurrentUser.getInstace().updateCurrentUser(CurrentUser.getInstace().getUser().getId());
                            Toast.makeText(getActivity(), "อัพเดทข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "ไม่สำเร็จ "+e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "onFailure: ", e);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("อัพโหลด "+(int)progress+"%");
                        }
                    });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.setting_option, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @OnClick(R.id.imageProfile)
    void selectImage(){
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
            profileImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), profileImageUri);
                imageProfile.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
