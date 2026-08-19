package br.com.lbcifras.domain.usecase

import br.com.lbcifras.domain.model.Song
import br.com.lbcifras.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow

class ObserveSongsUseCase(
    private val repository: SongRepository
) {
    operator fun invoke(query: String): Flow<List<Song>> = repository.observeSongs(query)
}
