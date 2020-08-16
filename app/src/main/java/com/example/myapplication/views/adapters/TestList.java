package com.example.myapplication.views.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.myapplication.models.Place;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestList extends ArrayAdapter<Place>
{
    private Activity context;
    private List<Place> placeList;
    private String categorySwitch;


    public TestList(Activity context, List<Place> placeList, String categorySwitch){
        super(context, R.layout.places_row, placeList);
        this.categorySwitch = categorySwitch;
        this.context = context;
        this.placeList = placeList;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String number = Integer.toString(position+1);
        StorageReference photoReference = storageReference.child("places").child(categorySwitch).child(number).child(number + ".png");
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.places_row, null, true);
        final ImageView imageViewList = (ImageView) listViewItem.findViewById(R.id.imglist);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.name_txtView);
        TextView textViewAddress = (TextView) listViewItem.findViewById(R.id.address_txtView);
        TextView textViewPhone = (TextView) listViewItem.findViewById(R.id.phone_txtView);
        TextView textViewWebsite = (TextView) listViewItem.findViewById(R.id.website);
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
