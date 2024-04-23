package es.uc3m.mobileApps.kritika.Profile;

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

public class ListsFragment extends Fragment {
    private RecyclerView rvLists;
    private List<MediaList> listsList;
    private ListsAdapter listsAdapter;

    int listsProcessed = 0;
    int totalLists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_lists_fragment, container, false);

        // Inicializa el RecyclerView y el adaptador
        rvLists = rootView.findViewById(R.id.rvLists);
        listsList = new ArrayList<>();
        listsAdapter = new ListsAdapter(getContext(), listsList);
        rvLists.setAdapter(listsAdapter);
        rvLists.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Realiza la consulta a Firebase Firestore para obtener las listas del usuario
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
                                        // Todas las operaciones asincrónicas han finalizado, ahora podemos imprimir los ratings
                                        for (MediaList l : listsList) {
                                            Log.d("ListsFragment", "List: " + l.getTitle() + " - " + l.getListName() + " - " + l.getImageUrl());
                                        }

                                        // Evita la excepción CalledFromWrongThreadException usando runOnUiThread.
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
                        // Manejar errores
                        Log.e("ListsFragment", "Error al obtener listas: " + task.getException());
                    }
                });
    }

    private void fetchMediaInfo(MediaList list, OnListProcessedListener listener) {

        List<String> mediaIds = list.getMediaIds();
        String firstMediaId = mediaIds.get(0);

        MediaInfoFetcher.fetchTitle(firstMediaId, list.getMediaType(), new MediaInfoFetcher.OnTitleFetchedListener() {
            @Override
            public void onTitleFetched(String title) {
                list.setTitle(title);
                if (list.getImageUrl() != null) {
                    // Si se han obtenido ambos, notifica que la operación del rating ha sido procesada
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
                    // Si se han obtenido ambos, notifica que la operación del rating ha sido procesada
                    listener.onListProcessed();
                }
            }

            @Override
            public void onImageUrlFetchFailed() {
                Log.e("ListsFragment", "Error al obtener la URL de la imagen del media");
            }
        });
    }

    public interface OnListProcessedListener {
        void onListProcessed();
    }
}

