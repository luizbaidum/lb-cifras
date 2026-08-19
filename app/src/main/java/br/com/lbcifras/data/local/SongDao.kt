package br.com.lbcifras.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Query(
        """
        SELECT * FROM songs
        WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%'
        ORDER BY title COLLATE NOCASE ASC
        """
    )
    fun observeSongs(query: String): Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(song: SongEntity)

    @Query("DELETE FROM songs WHERE id = :songId")
    suspend fun deleteById(songId: Long)
}
