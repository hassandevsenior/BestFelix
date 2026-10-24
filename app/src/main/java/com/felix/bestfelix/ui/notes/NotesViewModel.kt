package com.felix.bestfelix.ui.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.bestfelix.domain.model.Note
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val counts: Map<NoteCategory, Int> = emptyMap(),
    val total: Int = 0,
    val selected: NoteCategory? = null,
    val query: String = "",
    val loading: Boolean = true,
)

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /** Rubrique passée par un lien « Notes (n) » depuis un autre onglet. */
    private val selected = MutableStateFlow(
        savedStateHandle.get<String>(CATEGORY_ARG)
            ?.takeIf { it.isNotBlank() && it != NO_CATEGORY }
            ?.let { name -> NoteCategory.entries.firstOrNull { it.name == name } },
    )
    private val query = MutableStateFlow("")

    val uiState: StateFlow<NotesUiState> =
        combine(repository.observeNotes(), selected, query) { notes, category, search ->
            val needle = search.trim().lowercase()
            NotesUiState(
                notes = notes
                    .filter { category == null || it.category == category }
                    .filter { note ->
                        needle.isEmpty() ||
                            note.title?.lowercase()?.contains(needle) == true ||
                            note.body.lowercase().contains(needle)
                    },
                counts = NoteCategory.entries.associateWith { entry ->
                    notes.count { it.category == entry }
                },
                total = notes.size,
                selected = category,
                query = search,
                loading = false,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotesUiState(),
        )

    fun select(category: NoteCategory?) {
        selected.value = category
    }

    fun search(text: String) {
        query.value = text
    }

    fun create(category: NoteCategory, title: String?, body: String) {
        if (body.isBlank()) return
        viewModelScope.launch { repository.create(category, title, body) }
    }

    fun update(note: Note) {
        if (note.body.isBlank()) return
        viewModelScope.launch { repository.update(note) }
    }

    fun delete(note: Note) {
        viewModelScope.launch { repository.delete(note) }
    }

    companion object {
        const val CATEGORY_ARG = "rubrique"
        const val NO_CATEGORY = "toutes"
    }
}
