package es.uc3m.mobileApps.kritika.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import es.uc3m.mobileApps.kritika.model.MediaList;
import es.uc3m.mobileApps.kritika.R;

/**
 * Adapter class for the RecyclerView to display lists in the profile.
 */
public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ListViewHolder> {
    private Context context;
    private static List<MediaList> listsList;
    private LayoutInflater inflater;

    public ListsAdapter(Context context, List<MediaList> listsList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listsList = listsList;
    }

    /**
     * Interface to handle item click events.
     */
    public interface OnItemClickListener {
        void onItemClick(MediaList listsList);
    }

    private static ListsAdapter.OnItemClickListener listener;

    public void setOnItemClickListener(ListsAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.profile_list_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        MediaList list = listsList.get(position);

        String title = list.getListName();
        int maxLength = 30;
        if (title.length() > maxLength) {
            title = title.substring(0, maxLength) + "...";
        }
        holder.titleTextView.setText(title);

        holder.listTextView.setText(String.valueOf(list.getMediaIds().size() + " items in this list"));
        Glide.with(context).load(list.getImageUrl()).into(holder.mediaImageViewPoster);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(list);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listsList.size();
    }

    /**
     * ViewHolder class for the list item.
     */
    public static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, listTextView;
        ImageView mediaImageViewPoster;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvTitle);
            listTextView = itemView.findViewById(R.id.tvText);
            mediaImageViewPoster = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(listsList.get(position));
                    }
                }
            });
        }
    }
}
