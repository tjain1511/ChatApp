package com.indianapp.chatapp.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.indianapp.chatapp.Activities.ImageActivity;
import com.indianapp.chatapp.Activities.MainActivity;
import com.indianapp.chatapp.Activities.ResetPasswordActivity;
import com.indianapp.chatapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFrag extends Fragment {

    private View rootView;

    private TextView username;
    private TextView emailId;
    private TextView uploading;

    private CircleImageView userImage;
    private ImageView updateImg;
    private ImageView updateName;

    private Button logOut;
    private Button resetPassword;

    private Intent intent;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase db;

    private Uri imageUrl;

    private DatabaseReference reference;
    private ChildEventListener childEventListener;
    private DatabaseReference reference1;
    private ChildEventListener childEventListener1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateName = rootView.findViewById(R.id.updateName);
        userImage = rootView.findViewById(R.id.userImg);
        uploading = rootView.findViewById(R.id.uploading_prof);
        updateImg = rootView.findViewById(R.id.updateImg);
        username = rootView.findViewById(R.id.userName);
        emailId = rootView.findViewById(R.id.emailId);
        logOut = rootView.findViewById(R.id.logOut);
        resetPassword = rootView.findViewById(R.id.resetPassword);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        db = FirebaseDatabase.getInstance();

        emailId.setText(currentUser.getEmail());
        username.setText(currentUser.getDisplayName());
        Glide.with(getActivity())
                .load(currentUser.getPhotoUrl())
                .error(R.drawable.blank_profile)
                .apply(RequestOptions.circleCropTransform())
                .into(userImage);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ImageActivity.class);
                intent.putExtra("username", currentUser.getDisplayName());
                intent.putExtra("imageUrl", currentUser.getPhotoUrl().toString());
                startActivity(intent);
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.getReference().child("users").child(currentUser.getUid()).child("status").setValue("offline")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mAuth.signOut();
                                    Toast.makeText(getActivity(), "Logging out...", Toast.LENGTH_SHORT).show();
                                    intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getActivity(), "Some error occurred", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), ResetPasswordActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
            }
        });
        updateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                someActivityResultLauncher.launch(intent);
            }
        });
        updateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(username, "Username");
            }
        });
    }

    public void showDialog(TextView textView, String string) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.edit_dialog);
        TextView title = dialog.findViewById(R.id.title_edit);
        EditText edit = dialog.findViewById(R.id.edit_info);
        TextView cancel = dialog.findViewById(R.id.cancel_button);
        TextView save = dialog.findViewById(R.id.save_button);
        title.setText("Enter your " + string);
        edit.setText(textView.getText().toString());
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (string.equals("Username")) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(edit.getText().toString())
                            .build();
                    currentUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                db.getReference().child("users").child(currentUser.getUid())
                                        .child("username").setValue(edit.getText().toString());
                                reference = db.getReference().child("users").child(currentUser.getUid()).child("activeUsers");
                                childEventListener = new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        db.getReference().child("users").child(snapshot.getKey())
                                                .child("activeUsers").child(currentUser.getUid()).child("username").setValue(edit.getText().toString());
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }

                                };
                                textView.setText(currentUser.getDisplayName());
                                Toast.makeText(getActivity(), "Username Updated", Toast.LENGTH_SHORT).show();
                                db.getReference().child("users").child(currentUser.getUid())
                                        .child("activeUsers").addChildEventListener(childEventListener);
                            } else {
                                Toast.makeText(getActivity(), "Some Error occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        uploading.setVisibility(View.VISIBLE);
                        updateImg.setVisibility(View.INVISIBLE);
                        userImage.setVisibility(View.INVISIBLE);
                        Intent data = result.getData();
                        Uri selectImage = data.getData();
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        StorageReference reference = firebaseStorage.getReference().child("userImages").child(String.valueOf(UUID.randomUUID()));
                        Bitmap finalBitmap = bitmap;
                        reference.putBytes(byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            imageUrl = task.getResult();
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setPhotoUri(imageUrl)
                                                    .build();
                                            currentUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    db.getReference().child("users").child(currentUser.getUid())
                                                            .child("imageUrl").setValue(imageUrl.toString());
                                                    reference1 = db.getReference().child("users").child(currentUser.getUid()).child("activeUsers");
                                                    childEventListener1 = new ChildEventListener() {
                                                        @Override
                                                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                                            db.getReference().child("users").child(snapshot.getKey())
                                                                    .child("activeUsers").child(currentUser.getUid()).child("imageUrl").setValue(imageUrl.toString());
                                                        }

                                                        @Override
                                                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                                        }

                                                        @Override
                                                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                                        }

                                                        @Override
                                                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    };
                                                    reference1.addChildEventListener(childEventListener1);
                                                }
                                            });
                                            userImage.setImageBitmap(finalBitmap);
                                            userImage.setVisibility(View.VISIBLE);
                                            uploading.setVisibility(View.INVISIBLE);
                                            updateImg.setVisibility(View.VISIBLE);
                                        } else {
                                            Toast.makeText(getActivity(), "Some error occurred", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });

    @Override
    public void onDestroy() {
        if (reference != null) {
            reference.removeEventListener(childEventListener);
        }
        if (reference1 != null) {
            reference1.removeEventListener(childEventListener1);
        }
        super.onDestroy();
    }
}
