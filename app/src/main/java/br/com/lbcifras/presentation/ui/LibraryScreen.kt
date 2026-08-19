package br.com.lbcifras.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    onQueryChange: (String) -> Unit,
    onCreateSong: (title: String, artist: String, key: String, chordPro: String) -> Unit,
    onOpenSong: (songId: Long) -> Unit,
    onImportFromUrl: (String) -> Unit,
    onClearImportFeedback: () -> Unit
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showImportDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "LB Cifras") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Text(text = "+")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Buscar por titulo ou artista") },
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { showImportDialog = true }) {
                    Text(text = if (uiState.isImporting) "Importando..." else "Importar por Link")
                }

                if (uiState.importFeedback != null) {
                    TextButton(onClick = onClearImportFeedback) {
                        Text(text = "Limpar aviso")
                    }
                }
            }

            if (uiState.importFeedback != null) {
                Text(
                    text = uiState.importFeedback,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (uiState.songs.isEmpty()) {
                Text(
                    text = "Nenhuma cifra cadastrada ainda.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.songs, key = { it.id }) { song ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onOpenSong(song.id) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = song.title, style = MaterialTheme.typography.titleMedium)
                                Text(text = song.artist.ifBlank { "Artista desconhecido" })
                                Text(text = "Tom: ${song.musicalKey}")
                                Text(
                                    text = song.chordProText,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 4
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateSongDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { title, artist, key, chordPro ->
                onCreateSong(title, artist, key, chordPro)
                showCreateDialog = false
            }
        )
    }

    if (showImportDialog) {
        ImportSongDialog(
            isLoading = uiState.isImporting,
            onDismiss = { showImportDialog = false },
            onImport = { url ->
                onImportFromUrl(url)
                showImportDialog = false
            }
        )
    }
}

@Composable
private fun ImportSongDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onImport: (url: String) -> Unit
) {
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Importar por link") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text(text = "Link da cifra") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Text(
                    text = "Suporta links da Cifra Club e Ultimate Guitar.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !isLoading,
                onClick = { onImport(url) }
            ) {
                Text(text = if (isLoading) "Importando..." else "Importar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar")
            }
        }
    )
}

@Composable
private fun CreateSongDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, artist: String, key: String, chordPro: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("C") }
    var chordPro by remember { mutableStateOf("[C]Exemplo de [G]cifra") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Nova Cifra") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(text = "Titulo") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text(text = "Artista") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = key,
                        onValueChange = { key = it },
                        label = { Text(text = "Tom") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                OutlinedTextField(
                    value = chordPro,
                    onValueChange = { chordPro = it },
                    label = { Text(text = "Cifra (ChordPro)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 6
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(title, artist, key, chordPro) }) {
                Text(text = "Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar")
            }
        }
    )
}
