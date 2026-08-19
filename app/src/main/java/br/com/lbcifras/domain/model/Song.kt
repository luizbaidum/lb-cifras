package br.com.lbcifras.domain.model

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val musicalKey: String,
    val chordProText: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long
)
