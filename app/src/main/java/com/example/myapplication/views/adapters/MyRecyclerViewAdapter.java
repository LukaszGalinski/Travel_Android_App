package com.example.myapplication.views.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.models.Place;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Place> mData;
    private String category;
    private LayoutInflater mInflater;
    private addItemClickListener mClickListener;
    private Context context;

    public MyRecyclerViewAdapter(Context context, List<Place> data, String category) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.category = category;
        this.context = context;
    }

    public void setClickListener(addItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface addItemClickListener {
        void onItemClick(View view, int position, Place place);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.places_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull final ViewHolder holder, int position) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String number = Integer.toString(position + 1);
        StorageReference photoReference = storageReference.child("places").child(category).child(number).child(number + ".png");

        try {
            final File localFile = File.createTempFile("images", ".png");
            photoReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Picasso.with(context)
                        .load(localFile)
                        .fit()
                        .into(holder.imageViewList);
                Log.d("Loading Image: ", " Success!");
            }).addOnFailureListener(exception -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        Place place = mData.get(position);
        holder.textViewName.setText(place.getName());
        holder.textViewAddress.setText(place.getAddress());
        holder.textViewPhone.setText(place.getPhone());
        holder.textViewWebsite.setText(place.getWebsite());

        holder.itemView.setOnClickListener(v -> mClickListener.onItemClick(v, holder.getAdapterPosition(), getItem(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewList;
        TextView textViewName;
        TextView textViewAddress;
        TextView textViewPhone;
        TextView textViewWebsite;

        ViewHolder(View itemView) {
            super(itemView);
            imageViewList = itemView.findViewById(R.id.imglist);
            textViewName = itemView.findViewById(R.id.name_txtView);
            textViewAddress = itemView.findViewById(R.id.address_txtView);
            textViewPhone = itemView.findViewById(R.id.phone_txtView);
            textViewWebsite = itemView.findViewById(R.id.website);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition(), getItem(getAdapterPosition()));
        }
    }

    Place getItem(int id) {
        return mData.get(id);
    }


}