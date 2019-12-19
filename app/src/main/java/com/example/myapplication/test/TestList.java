package com.example.myapplication.test;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.ImageWriter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Place;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class TestList extends ArrayAdapter<Place>
{
    private Activity context;
    private List<Place> placeList;
    private String categorySwitch;


    public TestList(Activity context, List<Place> placeList, String categorySwitch){
        super(context, R.layout.arraylist_layout, placeList);
        this.categorySwitch = categorySwitch;
        this.context = context;
        this.placeList = placeList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){


        //fill the ListView
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String number = Integer.toString(position+1);

        StorageReference photoReference = storageReference.child("places").child(categorySwitch).child(number).child(number + ".png");
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.arraylist_layout, null, true);

        final ImageView imageViewList = (ImageView) listViewItem.findViewById(R.id.imglist);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.name_txtView);
        TextView textViewAddress = (TextView) listViewItem.findViewById(R.id.address_txtView);
        TextView textViewPhone = (TextView) listViewItem.findViewById(R.id.phone_txtView);
        TextView textViewWebsite = (TextView) listViewItem.findViewById(R.id.website);

        /*photoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext())
                        .load(uri)
                        .fit()
                        .into(imageViewList);
                Log.d("Loading Image: "," Success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Loading Image: "," Failed!");
            }
        });
        */


        try {
            final File localFile = File.createTempFile("images", ".png");


            photoReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Picasso.with(getContext())
                            .load(localFile)
                            .fit()
                            .into(imageViewList);
                    Log.d("Loading Image: ", " Success!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }

        Place place = placeList.get(position);
        textViewName.setText(place.getName());
        textViewAddress.setText(place.getAddress());
        textViewPhone.setText(place.getPhone());
        textViewWebsite.setText(place.getWebsite());

        return listViewItem;
    }
}
