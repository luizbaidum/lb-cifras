package br.com.lbcifras.presentation.ui

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.lbcifras.LbCifrasApp
import br.com.lbcifras.domain.usecase.AddSongUseCase
import br.com.lbcifras.domain.usecase.DeleteSongUseCase
import br.com.lbcifras.domain.usecase.ObserveSongsUseCase
import br.com.lbcifras.domain.usecase.UpdateSongUseCase

@Composable
fun LbCifrasRoot(application: Application) {
    val app = application as LbCifrasApp

    val vm: LibraryViewModel = viewModel(
        factory = LibraryViewModel.factory(
            observeSongsUseCase = ObserveSongsUseCase(app.songRepository),
            addSongUseCase = AddSongUseCase(app.songRepository),
            updateSongUseCase = UpdateSongUseCase(app.songRepository),
            deleteSongUseCase = DeleteSongUseCase(app.songRepository),
            chordLinkImporter = app.chordLinkImporter
        )
    )

    val state by vm.uiState.collectAsStateWithLifecycle()
    var selectedSongId by remember { mutableStateOf<Long?>(null) }

    val selectedSong = state.songs.firstOrNull { it.id == selectedSongId }

    if (selectedSong != null) {
        ReaderScreen(
            song = selectedSong,
            onBack = { selectedSongId = null },
            onSaveEdit = { songId, title, artist, key, chordPro ->
                vm.updateSong(
                    songId = songId,
                    title = title,
                    artist = artist,
                    key = key,
                    chordPro = chordPro,
                    createdAtEpochMillis = selectedSong.createdAtEpochMillis
                )
            },
            onDelete = { songId ->
                vm.deleteSong(songId)
                selectedSongId = null
            }
        )
    } else {
        LibraryScreen(
            uiState = state,
            onQueryChange = vm::onQueryChange,
            onCreateSong = vm::addSong,
            onOpenSong = { songId -> selectedSongId = songId },
            onImportFromUrl = vm::importSongFromUrl,
            onClearImportFeedback = vm::clearImportFeedback
        )
    }
}
