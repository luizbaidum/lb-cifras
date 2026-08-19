package br.com.lbcifras.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.lbcifras.data.importer.ChordLinkImporter
import br.com.lbcifras.domain.model.Song
import br.com.lbcifras.domain.usecase.AddSongUseCase
import br.com.lbcifras.domain.usecase.DeleteSongUseCase
import br.com.lbcifras.domain.usecase.ObserveSongsUseCase
import br.com.lbcifras.domain.usecase.UpdateSongUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryViewModel(
    private val observeSongs: ObserveSongsUseCase,
    private val addSong: AddSongUseCase,
    private val updateSongUseCase: UpdateSongUseCase,
    private val deleteSongUseCase: DeleteSongUseCase,
    private val chordLinkImporter: ChordLinkImporter
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val isImporting = MutableStateFlow(false)
    private val importFeedback = MutableStateFlow<String?>(null)

    val uiState: StateFlow<LibraryUiState> = combine(
        query,
        query.flatMapLatest { currentQuery -> observeSongs(currentQuery) },
        isImporting,
        importFeedback
    ) { currentQuery, songs, currentlyImporting, feedback ->
                LibraryUiState(
                    query = currentQuery,
                    songs = songs,
                    isLoading = false,
                    isImporting = currentlyImporting,
                    importFeedback = feedback
                )
            }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LibraryUiState()
        )

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    fun addSong(title: String, artist: String, key: String, chordPro: String) {
        if (title.isBlank() || chordPro.isBlank()) return

        val now = System.currentTimeMillis()
        val song = Song(
            id = 0,
            title = title.trim(),
            artist = artist.trim(),
            musicalKey = key.trim().ifBlank { "C" },
            chordProText = chordPro.trim(),
            createdAtEpochMillis = now,
            updatedAtEpochMillis = now
        )

        viewModelScope.launch {
            addSong(song)
        }
    }

    fun updateSong(
        songId: Long,
        title: String,
        artist: String,
        key: String,
        chordPro: String,
        createdAtEpochMillis: Long
    ) {
        if (title.isBlank() || chordPro.isBlank()) return

        val now = System.currentTimeMillis()
        val song = Song(
            id = songId,
            title = title.trim(),
            artist = artist.trim(),
            musicalKey = key.trim().ifBlank { "C" },
            chordProText = chordPro.trim(),
            createdAtEpochMillis = createdAtEpochMillis,
            updatedAtEpochMillis = now
        )

        viewModelScope.launch {
            updateSongUseCase(song)
        }
    }

    fun deleteSong(songId: Long) {
        viewModelScope.launch {
            deleteSongUseCase(songId)
        }
    }

    fun importSongFromUrl(url: String) {
        if (url.isBlank()) {
            importFeedback.value = "Cole um link antes de importar."
            return
        }

        viewModelScope.launch {
            isImporting.value = true
            importFeedback.value = null

            val result = withContext(Dispatchers.IO) {
                chordLinkImporter.importFromUrl(url)
            }

            result.fold(
                onSuccess = { importedSong ->
                    addSong(
                        title = importedSong.title,
                        artist = importedSong.artist,
                        key = importedSong.musicalKey,
                        chordPro = importedSong.chordProText
                    )
                    importFeedback.value = "Cifra importada com sucesso: ${importedSong.title}"
                },
                onFailure = { throwable ->
                    importFeedback.value = throwable.message ?: "Falha ao importar a cifra do link"
                }
            )

            isImporting.value = false
        }
    }

    fun clearImportFeedback() {
        importFeedback.value = null
    }

    companion object {
        fun factory(
            observeSongsUseCase: ObserveSongsUseCase,
            addSongUseCase: AddSongUseCase,
            updateSongUseCase: UpdateSongUseCase,
            deleteSongUseCase: DeleteSongUseCase,
            chordLinkImporter: ChordLinkImporter
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LibraryViewModel(
                        observeSongsUseCase,
                        addSongUseCase,
                        updateSongUseCase,
                        deleteSongUseCase,
                        chordLinkImporter
                    ) as T
                }
            }
        }
    }
}
