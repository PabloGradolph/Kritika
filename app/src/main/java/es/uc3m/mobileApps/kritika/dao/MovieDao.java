package es.uc3m.mobileApps.kritika.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import es.uc3m.mobileApps.kritika.model.Movie;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movies")
    List<Movie> getAll();

    @Query("SELECT * FROM movies WHERE id = :id")
    Movie getById(int id);

    @Insert
    void insertAll(Movie... movies);

    @Update
    void update(Movie movie);

    @Delete
    void delete(Movie movie);
}

