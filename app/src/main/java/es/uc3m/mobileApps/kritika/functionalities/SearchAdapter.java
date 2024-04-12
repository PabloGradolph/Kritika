package es.uc3m.mobileApps.kritika.functionalities;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.SearchInterface;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchableViewHolder> {
    private List<SearchInterface> items;

    public SearchAdapter(List<SearchInterface> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public SearchableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new SearchableViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchableViewHolder holder, int position) {
        SearchInterface item = items.get(position);
        Log.i("item", String.valueOf(item.getTitle()));
        holder.titleTextView.setText(item.getTitle());
        //holder.subtitleTextView.setText(item.getSubtitle());
        // Cargar imagen con Glide
        Glide.with(holder.imageView.getContext())
                .load(item.getImagePath())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateData(List<SearchInterface> newSearchables) {
        this.items.clear(); // Limpia los datos existentes
        this.items.addAll(newSearchables); // Agrega los nuevos datos
        notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
    }

    static class SearchableViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;


        public SearchableViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            //subtitleTextView = itemView.findViewById(R.id.subtitleTextView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

