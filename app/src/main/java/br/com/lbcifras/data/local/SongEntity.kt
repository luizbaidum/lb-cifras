package br.com.lbcifras.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val artist: String,
    val musicalKey: String,
    val chordProText: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long
)
