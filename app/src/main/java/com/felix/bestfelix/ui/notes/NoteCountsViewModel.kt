package com.felix.bestfelix.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Alimente les liens « Notes (n) » affichés dans les autres onglets. */
@HiltViewModel
class NoteCountsViewModel @Inject constructor(
    repository: NoteRepository,
) : ViewModel() {

    val counts: StateFlow<Map<NoteCategory, Int>> = repository.observeNotes()
        .map { notes ->
            NoteCategory.entries.associateWith { entry -> notes.count { it.category == entry } }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap(),
        )
}
