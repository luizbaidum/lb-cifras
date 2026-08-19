package br.com.lbcifras.data.repository

import br.com.lbcifras.data.local.SongDao
import br.com.lbcifras.data.local.SongEntity
import br.com.lbcifras.domain.model.Song
import br.com.lbcifras.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineSongRepository(
    private val songDao: SongDao
) : SongRepository {

    override fun observeSongs(query: String): Flow<List<Song>> {
        return songDao.observeSongs(query).map { entities ->
            entities.map { entity ->
                Song(
                    id = entity.id,
                    title = entity.title,
                    artist = entity.artist,
                    musicalKey = entity.musicalKey,
                    chordProText = entity.chordProText,
                    createdAtEpochMillis = entity.createdAtEpochMillis,
                    updatedAtEpochMillis = entity.updatedAtEpochMillis
                )
            }
        }
    }

    override suspend fun addSong(song: Song) {
        songDao.upsert(
            SongEntity(
                id = song.id,
                title = song.title,
                artist = song.artist,
                musicalKey = song.musicalKey,
                chordProText = song.chordProText,
                createdAtEpochMillis = song.createdAtEpochMillis,
                updatedAtEpochMillis = song.updatedAtEpochMillis
            )
        )
    }

    override suspend fun deleteSong(songId: Long) {
        songDao.deleteById(songId)
    }
}
