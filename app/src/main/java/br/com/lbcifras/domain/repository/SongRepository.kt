package br.com.lbcifras.domain.repository

import br.com.lbcifras.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun observeSongs(query: String): Flow<List<Song>>
    suspend fun addSong(song: Song)
    suspend fun deleteSong(songId: Long)
}
