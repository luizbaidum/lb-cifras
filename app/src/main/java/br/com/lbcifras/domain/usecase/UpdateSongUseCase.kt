package br.com.lbcifras.domain.usecase

import br.com.lbcifras.domain.model.Song
import br.com.lbcifras.domain.repository.SongRepository

class UpdateSongUseCase(
    private val repository: SongRepository
) {
    suspend operator fun invoke(song: Song) {
        repository.addSong(song)
    }
}
