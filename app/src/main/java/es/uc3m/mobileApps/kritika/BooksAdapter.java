package es.uc3m.mobileApps.kritika;

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
import es.uc3m.mobileApps.kritika.model.Book;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {
    private List<Book> books;
    private LayoutInflater inflater;

    public BooksAdapter(Context context, List<Book> books) {
        this.inflater = LayoutInflater.from(context);
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.book_item, parent, false);
        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book currentBook = books.get(position);
        holder.tvTitle.setText(currentBook.getTitle());
        holder.tvAuthors.setText(String.join(", ", currentBook.getAuthors()));
        String shortDescription = currentBook.getDescription().length() > 100
                ? currentBook.getDescription().substring(0, 100) + "..."
                : currentBook.getDescription();
        holder.tvDescription.setText(shortDescription);
        // Cargar imagen de portada con Glide
        Glide.with(holder.imageViewThumbnail.getContext())
                .load(currentBook.getThumbnail())
                .into(holder.imageViewThumbnail);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvAuthors, tvDescription;
        final ImageView imageViewThumbnail;

        BookViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthors = itemView.findViewById(R.id.tvAuthors);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
        }
    }
}

