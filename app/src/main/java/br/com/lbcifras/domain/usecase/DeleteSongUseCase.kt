package br.com.lbcifras.domain.usecase

import br.com.lbcifras.domain.repository.SongRepository

class DeleteSongUseCase(
    private val repository: SongRepository
) {
    suspend operator fun invoke(songId: Long) {
        repository.deleteSong(songId)
    }
}
