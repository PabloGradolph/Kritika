package es.uc3m.mobileApps.kritika.books;

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

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Book;

/**
 * Adapter class for displaying a list of books in a RecyclerView.
 */
public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {
    private List<Book> books;
    private LayoutInflater inflater;

    /**
     * Constructor for the BooksAdapter.
     *
     * @param context The context of the activity or fragment.
     * @param books   The list of books to be displayed.
     */
    public BooksAdapter(Context context, List<Book> books) {
        this.inflater = LayoutInflater.from(context);
        this.books = books;
    }

    /**
     * Interface to handle item click events.
     */
    public interface OnItemClickListener {
        /**
         * Method invoked when a book item is clicked.
         *
         * @param book The book that was clicked.
         */
        void onItemClick(Book book);
    }
    private static BooksAdapter.OnItemClickListener listener;

    /**
     * Sets the click listener for the adapter.
     *
     * @param listener The listener to be set.
     */
    public void setOnItemClickListener(BooksAdapter.OnItemClickListener listener) {this.listener = listener;}

    /**
     * Inflates the layout for a single book item view and returns a new BookViewHolder.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The type of the view.
     * @return A new BookViewHolder instance.
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.book_item, parent, false);
        return new BookViewHolder(itemView);
    }

    /**
     * Binds the data of a specific book to the corresponding views in the BookViewHolder.
     *
     * @param holder The BookViewHolder to bind data to.
     * @param position The position of the book in the dataset.
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book currentBook = books.get(position);
        holder.tvTitle.setText(currentBook.getTitle());
        holder.tvAuthors.setText(String.join(", ", currentBook.getAuthors()));
        String shortDescription = currentBook.getDescription().length() > 100
                ? currentBook.getDescription().substring(0, 100) + "..."
                : currentBook.getDescription();
        holder.tvDescription.setText(shortDescription);
        // Load cover image using Glide
        Glide.with(holder.imageViewThumbnail.getContext())
                .load(currentBook.getThumbnail())
                .into(holder.imageViewThumbnail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(currentBook);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    /**
     * ViewHolder class for holding the views associated with a single book item.
     */
    static class BookViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvAuthors, tvDescription;
        final ImageView imageViewThumbnail;

        /**
         * Constructor for the BookViewHolder.
         *
         * @param itemView The view associated with a single book item.
         */
        BookViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthors = itemView.findViewById(R.id.tvAuthors);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
        }
    }
}

