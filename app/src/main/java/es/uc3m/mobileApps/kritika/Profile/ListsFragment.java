package es.uc3m.mobileApps.kritika.Profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.MediaList;

/**
 * Fragment for displaying lists associated with the user's profile.
 */
public class ListsFragment extends Fragment {
    private RecyclerView rvLists;
    private List<MediaList> listsList;
    private ListsAdapter listsAdapter;
    int listsProcessed = 0;
    int totalLists;
    private Context fragmentContext;
    private String selectedMediaType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_lists_fragment, container, false);

        // Initialize RecyclerView and adapter
        rvLists = rootView.findViewById(R.id.rvLists);
        listsList = new ArrayList<>();
        listsAdapter = new ListsAdapter(getContext(), listsList);
        rvLists.setAdapter(listsAdapter);
        rvLists.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        listsAdapter.setOnItemClickListener(new ListsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MediaList list) {
                fragmentContext = getContext();
                selectedMediaType = list.getMediaType();

                showMediaTitlesDialog(fragmentContext, list.getMediaIds(), selectedMediaType);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Query Firebase Firestore to fetch user's lists
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("lists")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MediaList list = document.toObject(MediaList.class);
                            Log.d("ListsFragment", "Lists: " + list.getListName());
                            listsList.add(list);
                        }
                        totalLists = listsList.size();

                        for (MediaList list : listsList) {
                            fetchMediaInfo(list, new OnListProcessedListener() {

                                @Override
                                public void onListProcessed() {
                                    listsProcessed++;
                                    if (listsProcessed == totalLists) {
                                        // All asynchronous operations have finished, now we can print the lists
                                        for (MediaList l : listsList) {
                                            Log.d("ListsFragment", "List: " + l.getTitle() + " - " + l.getListName() + " - " + l.getImageUrl());
                                        }

                                        // Avoid CalledFromWrongThreadException using runOnUiThread.
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                listsAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    } else {
                        Log.e("ListsFragment", "Error al obtener listas: " + task.getException());
                    }
                });
    }

    /**
     * Fetches additional media information for the given list.
     * @param list The media list to fetch information for.
     * @param listener Listener to be notified when the processing of the list is complete.
     */
    private void fetchMediaInfo(MediaList list, OnListProcessedListener listener) {

        List<String> mediaIds = list.getMediaIds();
        // Check if mediaIds list is empty
        if (mediaIds == null || mediaIds.isEmpty()) {
            // Empty list, notify that list operation has been processed
            listener.onListProcessed();
            return;
        }

        String firstMediaId = mediaIds.get(0);

        MediaInfoFetcher.fetchTitle(firstMediaId, list.getMediaType(), new MediaInfoFetcher.OnTitleFetchedListener() {
            @Override
            public void onTitleFetched(String title) {
                list.setTitle(title);
                if (list.getImageUrl() != null) {
                    // If both are obtained, notify that rating operation has been processed
                    listener.onListProcessed();
                }
            }

            @Override
            public void onTitleFetchFailed() {
                Log.e("ListsFragment", "Error al obtener el título del media");
            }
        });

        MediaInfoFetcher.fetchImageUrl(firstMediaId, list.getMediaType(), new MediaInfoFetcher.OnImageUrlFetchedListener() {

            @Override
            public void onImageUrlFetched(String imageUrl) {
                list.setImageUrl(imageUrl);
                if (list.getTitle() != null) {
                    // If both are obtained, notify that rating operation has been processed
                    listener.onListProcessed();
                }
            }

            @Override
            public void onImageUrlFetchFailed() {
                Log.e("ListsFragment", "Error al obtener la URL de la imagen del media");
            }
        });
    }

    /**
     * Interface for notifying when a list has been processed.
     */
    public interface OnListProcessedListener {
        void onListProcessed();
    }

    /**
     * Displays a dialog with media titles.
     * @param context The context to display the dialog in.
     * @param mediaIds The list of media IDs.
     * @param mediaType The type of media.
     */
    private static void showMediaTitlesDialog(Context context, List<String> mediaIds, String mediaType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Media Titles");

        // Create a string with media titles
        StringBuilder stringBuilder = new StringBuilder();

        // Variable to keep track of how many titles have been fetched
        final int[] titlesFetched = {0};

        // Iterate over each mediaId and fetch its title using MediaInfoFetcher
        for (int i = 0; i < mediaIds.size(); i++) {
            String mediaId = mediaIds.get(i);
            MediaInfoFetcher.fetchTitle(mediaId, mediaType, new MediaInfoFetcher.OnTitleFetchedListener() {
                @Override
                public void onTitleFetched(String title) {
                    // Add the title to the string
                    stringBuilder.append("- ").append(title).append("\n");

                    // Increment the counter of fetched titles
                    titlesFetched[0]++;

                    // Check if all titles have been fetched
                    if (titlesFetched[0] == mediaIds.size()) {
                        // All titles have been fetched, so update dialog message and show the dialog
                        builder.setMessage(stringBuilder.toString());
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }

                @Override
                public void onTitleFetchFailed() {
                    // Handle case where title couldn't be fetched
                    Log.e("showMediaTitlesDialog", "Error fetching title for mediaId: " + mediaId);

                    // Increment the counter of fetched titles even if title fetching fails
                    titlesFetched[0]++;

                    // Check if all titles have been fetched
                    if (titlesFetched[0] == mediaIds.size()) {
                        // All titles have been fetched (even if some failed), so update dialog message and show the dialog
                        builder.setMessage(stringBuilder.toString());
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        }
    }
}

