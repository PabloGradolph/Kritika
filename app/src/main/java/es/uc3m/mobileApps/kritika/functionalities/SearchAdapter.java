package es.uc3m.mobileApps.kritika.functionalities;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import es.uc3m.mobileApps.kritika.Misc.ApiConstants;
import es.uc3m.mobileApps.kritika.Profile.MediaInfoFetcher;
import es.uc3m.mobileApps.kritika.R;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchableViewHolder> {
    private List<Object> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    /**
     * Adapter for showing details of media of the search query.
     */
    public SearchAdapter(List<Object> items) {
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
        DocumentSnapshot item = (DocumentSnapshot) items.get(position);
        Log.d("SearchAdapter Data", item.getId() + " => " + item.getData());

        String type = item.getString("type");
        if (type != null) {
            switch (type) {
                case "movies":
                    holder.tvArtistName.setText(item.getString("overview"));
                    break;
                case "songs":
                    handleArtists(item, holder.tvArtistName);
                    break;
                case "books":
                    String authors = item.getString("authors");
                    holder.tvArtistName.setText(formatAuthors(authors));
                    break;
            }

            holder.titleTextView.setText(item.getString("title"));

            // Check and fetch the image URL appropriately
            if (item.contains("image")) {
                loadIntoGlide(holder.imageView, item.getString("image"));
            } else if (item.contains("imagePath")) {
                fetchImageBasedOnType(item.getString("imagePath"), type, holder.imageView);
            } else {
                fetchImageUrlFromAPI(item.getId(), type, holder.imageView);
            }
        } else {
            holder.tvArtistName.setText("No type specified");
            holder.titleTextView.setText("Unknown Item");
            holder.imageView.setImageResource(R.drawable.default_user_picture);  // Ensure you have this default image in your resources
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

    }

    private void handleArtists(DocumentSnapshot item, TextView tvArtistName) {
        if (item.contains("artists")) {
            List<String> artists = (List<String>) item.get("artists");
            String artistsText = String.join(", ", artists);
            tvArtistName.setText(artistsText);
        } else if (item.contains("artistName")) {
            tvArtistName.setText(item.getString("artistName"));
        } else if (item.contains("track_artist")) {
            tvArtistName.setText(item.getString("track_artist"));
        } else {
            tvArtistName.setText("No artists listed");
        }
    }

    private void fetchImageBasedOnType(String imagePath, String type, ImageView imageView) {
        if (type.equals("movies")) {
            imagePath = ApiConstants.MOVIEDB_IMAGE_URL + imagePath;
        }
        loadIntoGlide(imageView, imagePath);
    }

    private void fetchImageUrlFromAPI(String mediaId, String type, ImageView imageView) {
        MediaInfoFetcher.fetchImageUrl(mediaId, type, new MediaInfoFetcher.OnImageUrlFetchedListener() {
            @Override
            public void onImageUrlFetched(String imageUrl) {
                loadIntoGlide(imageView, imageUrl);
            }

            @Override
            public void onImageUrlFetchFailed() {
                imageView.setImageResource(R.drawable.default_user_picture);  // Load default image on failure
            }
        });
    }


    private void loadIntoGlide(ImageView imageView, String url) {
        // Create a handler that uses the Main Looper to ensure updates are done on the main thread
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            Glide.with(imageView.getContext())
                    .load(url != null ? url : R.drawable.default_user_picture) // Use a default image if the URL is null
                    .placeholder(R.drawable.default_user_picture) // Optional: placeholder while loading
                    .error(R.drawable.default_user_picture) // Optional: error image if load fails
                    .into(imageView);
        });
    }




    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatAuthors(String authors) {
        if (authors == null || authors.isEmpty()) {
            return "No authors listed";
        }
        // Elimina los corchetes y divide la cadena en un array por comas
        String[] authorsArray = authors.replace("[", "")
                .replace("]", "")
                .replace("'", "")  // Elimina comillas simples que pueden envolver los nombres
                .trim()            // Elimina espacios blancos adicionales
                .split("\\s*,\\s*"); // Divide por comas y espacios

        // Convierte el array en una lista y únela con comas
        return Arrays.stream(authorsArray)
                .collect(Collectors.joining(", "));
    }

    public void updateData(List<Object> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }

    static class SearchableViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView tvArtistName;

        public SearchableViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

}

