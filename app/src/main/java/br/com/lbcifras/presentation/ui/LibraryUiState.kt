package br.com.lbcifras.presentation.ui

import br.com.lbcifras.domain.model.Song

data class LibraryUiState(
    val query: String = "",
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = true,
    val isImporting: Boolean = false,
    val importFeedback: String? = null
)
