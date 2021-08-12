package com.example.chatapps2021.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapps2021.R;
import com.example.chatapps2021.UsersModel.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {


    private List<Users> mUsers;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private String currentUser;
    private TextView username;
    private CircleImageView profileIamge;

     private StorageReference storageReference;
     private StorageTask uploadTask;
     private Uri imageUri;

     private static final int IMAGE_REQUEST_CODE=1;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_user_profile, container, false);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        currentUser=firebaseUser.getUid();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(currentUser);
        storageReference= FirebaseStorage.getInstance().getReference("uploads");

        username=view.findViewById(R.id.userName);
        profileIamge=view.findViewById(R.id.profile_image_user);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                Users users=snapshot.getValue(Users.class);

                username.setText(users.getName());

                if(users.getImageUrl().equals("default")){
                    profileIamge.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Glide.with(getContext()).load(users.getImageUrl()).into(profileIamge);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        profileIamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIamge();
            }
        });




        return view;
    }

    private void openIamge() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST_CODE);
    }

    private String getFileExtension(Uri uri){

        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));


    }

    // image upload method

    private void uploadImage(){

        ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();


        if (imageUri !=null){
           final StorageReference filereference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
           uploadTask=filereference.putFile(imageUri);
           uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
               @Override
               public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                   if (!task.isSuccessful()){
                       throw task.getException();
                   }
                   return filereference.getDownloadUrl();
               }
           }).addOnCompleteListener(new OnCompleteListener() {
               @Override
               public void onComplete(@NonNull Task task) {

                   if(task.isSuccessful()){

                       Uri downloadUri= (Uri) task.getResult();
                       String mUri=downloadUri.toString();

                       reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                       HashMap<String,Object> hashMap=new HashMap<>();
                       hashMap.put("imageUrl",mUri);

                       reference.updateChildren(hashMap);
                       progressDialog.dismiss();

                   }

                   else {
                       Toast.makeText(getContext(), "Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                       progressDialog.dismiss();
                   }

               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(getContext(), "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                   progressDialog.dismiss();
               }
           });


        }
        
        else {
            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==IMAGE_REQUEST_CODE && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();

            if (uploadTask!=null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload is progress", Toast.LENGTH_SHORT).show();
            }
            else {
                uploadImage();
            }


        }

    }
}