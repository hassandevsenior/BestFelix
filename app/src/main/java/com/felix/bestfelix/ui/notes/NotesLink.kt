package com.felix.bestfelix.ui.notes

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felix.bestfelix.domain.model.NoteCategory

/** « Notes (3) » : ouvre le bloc-notes déjà filtré sur la rubrique de la section. */
@Composable
fun NotesLink(
    category: NoteCategory,
    onOpenNotes: (NoteCategory) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteCountsViewModel = hiltViewModel(),
) {
    val counts by viewModel.counts.collectAsStateWithLifecycle()
    val count = counts[category] ?: 0

    TextButton(onClick = { onOpenNotes(category) }, modifier = modifier) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.StickyNote2,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Text(text = "Notes ($count)", modifier = Modifier.padding(start = 6.dp))
    }
}
