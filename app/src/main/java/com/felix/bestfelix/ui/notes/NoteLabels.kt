package com.felix.bestfelix.ui.notes

import com.felix.bestfelix.domain.model.NoteCategory

val NoteCategory.label: String
    get() = when (this) {
        NoteCategory.GOOD_HABITS -> "Habitudes à prendre"
        NoteCategory.QUIT_HABITS -> "Habitudes à abandonner"
        NoteCategory.FINANCE -> "Finances"
        NoteCategory.PROJECTS -> "Projets pro"
        NoteCategory.GENERAL -> "Général"
    }

/** Libellé court, pour les pastilles des cartes. */
val NoteCategory.shortLabel: String
    get() = when (this) {
        NoteCategory.GOOD_HABITS -> "À prendre"
        NoteCategory.QUIT_HABITS -> "À abandonner"
        NoteCategory.FINANCE -> "Finances"
        NoteCategory.PROJECTS -> "Projets"
        NoteCategory.GENERAL -> "Général"
    }
