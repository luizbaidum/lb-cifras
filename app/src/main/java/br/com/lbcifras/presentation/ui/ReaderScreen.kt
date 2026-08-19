package br.com.lbcifras.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import br.com.lbcifras.domain.model.Song
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    song: Song,
    onBack: () -> Unit,
    onSaveEdit: (songId: Long, title: String, artist: String, key: String, chordPro: String) -> Unit,
    onDelete: (songId: Long) -> Unit
) {
    val scrollState = rememberScrollState()
    var isAutoScrollOn by remember { mutableStateOf(false) }
    var speedPxPerSecond by remember { mutableFloatStateOf(36f) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isAutoScrollOn, speedPxPerSecond) {
        if (!isAutoScrollOn) return@LaunchedEffect

        var lastFrameNanos = 0L
        while (isActive && isAutoScrollOn) {
            val frameNanos = withFrameNanos { it }
            if (lastFrameNanos > 0L) {
                val deltaNanos = frameNanos - lastFrameNanos
                val deltaSeconds = deltaNanos / 1_000_000_000f
                val deltaPx = speedPxPerSecond * deltaSeconds
                val consumed = scrollState.scrollBy(deltaPx)
                if (consumed <= 0f && scrollState.value >= scrollState.maxValue) {
                    isAutoScrollOn = false
                }
            }

            lastFrameNanos = frameNanos
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = song.title) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = song.artist.ifBlank { "Artista desconhecido" },
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "Tom: ${song.musicalKey}")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { isAutoScrollOn = !isAutoScrollOn }) {
                    Text(text = if (isAutoScrollOn) "Pausar" else "Auto-scroll")
                }
                Button(onClick = { speedPxPerSecond = (speedPxPerSecond - 12f).coerceAtLeast(12f) }) {
                    Text(text = "-")
                }
                Button(onClick = { speedPxPerSecond = (speedPxPerSecond + 12f).coerceAtMost(240f) }) {
                    Text(text = "+")
                }
                Button(onClick = { showEditDialog = true }) {
                    Text(text = "Editar")
                }
                Button(onClick = { showDeleteDialog = true }) {
                    Text(text = "Excluir")
                }
            }

            Text(
                text = "Velocidade: ${speedPxPerSecond.toInt()} px/s",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = buildChordProAnnotated(song.chordProText),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState),
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    if (showEditDialog) {
        EditSongDialog(
            song = song,
            onDismiss = { showEditDialog = false },
            onSave = { title, artist, key, chordPro ->
                onSaveEdit(song.id, title, artist, key, chordPro)
                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Excluir cifra") },
            text = { Text(text = "Tem certeza que deseja excluir esta cifra?") },
            confirmButton = {
                Button(onClick = {
                    onDelete(song.id)
                    showDeleteDialog = false
                }) {
                    Text(text = "Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = "Cancelar")
                }
            }
        )
    }
}

@Composable
private fun EditSongDialog(
    song: Song,
    onDismiss: () -> Unit,
    onSave: (title: String, artist: String, key: String, chordPro: String) -> Unit
) {
    var title by remember(song.id) { mutableStateOf(song.title) }
    var artist by remember(song.id) { mutableStateOf(song.artist) }
    var key by remember(song.id) { mutableStateOf(song.musicalKey) }
    var chordPro by remember(song.id) { mutableStateOf(song.chordProText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar cifra") },
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
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text(text = "Tom") },
                    modifier = Modifier.fillMaxWidth()
                )
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

private fun buildChordProAnnotated(chordProText: String): AnnotatedString {
    val chordColor = Color(0xFF1565C0)

    return buildAnnotatedString {
        val regex = Regex("\\[([^\\]]+)]")
        var currentIndex = 0

        regex.findAll(chordProText).forEach { match ->
            if (match.range.first > currentIndex) {
                append(chordProText.substring(currentIndex, match.range.first))
            }

            val chord = match.groupValues.getOrNull(1).orEmpty()

            pushStyle(SpanStyle(color = chordColor))
            append("[$chord]")
            pop()

            currentIndex = match.range.last + 1
        }

        if (currentIndex < chordProText.length) {
            append(chordProText.substring(currentIndex))
        }
    }
}
