package br.com.lbcifras.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
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
    var showControls by remember { mutableStateOf(false) }
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

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = buildChordProAnnotated(song.chordProText),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .padding(top = 64.dp, bottom = if (showControls) 170.dp else 16.dp)
                .verticalScroll(scrollState)
                .clickable { showControls = !showControls },
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodyLarge
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding(),
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TextButton(onClick = onBack) {
                    Text(text = "Voltar")
                }

                Text(
                    text = song.title,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall
                )

                TextButton(onClick = { showControls = !showControls }) {
                    Text(text = if (showControls) "Ocultar" else "Menu")
                }
            }
        }

        AnimatedVisibility(
            visible = showControls,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .navigationBarsPadding(),
                tonalElevation = 4.dp,
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${song.artist.ifBlank { "Artista desconhecido" }}  |  Tom ${song.musicalKey}",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { isAutoScrollOn = !isAutoScrollOn }) {
                            Text(text = if (isAutoScrollOn) "Pausar" else "Rolar")
                        }
                        Button(onClick = { speedPxPerSecond = (speedPxPerSecond - 12f).coerceAtLeast(12f) }) {
                            Text(text = "-")
                        }
                        Text(
                            text = "${speedPxPerSecond.toInt()} px/s",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .width(84.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Button(onClick = { speedPxPerSecond = (speedPxPerSecond + 12f).coerceAtMost(240f) }) {
                            Text(text = "+")
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showEditDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Editar")
                        }
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Excluir")
                        }
                    }
                }
            }
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
