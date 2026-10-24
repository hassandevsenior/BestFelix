package com.felix.bestfelix.data.db

import androidx.room.TypeConverter
import com.felix.bestfelix.domain.model.CreditDirection
import com.felix.bestfelix.domain.model.DayStatus
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.domain.model.Priority
import com.felix.bestfelix.domain.model.ProjectStatus
import java.time.Instant
import java.time.LocalDate

/**
 * Les dates sont stockées en jours époque (et non en millisecondes) : aucun calcul de durée
 * ne passe par des millis dans l'appli. Les énumérations sont stockées par leur nom, pour
 * qu'un réordonnancement du code ne corrompe pas la base.
 */
class Converters {

    @TypeConverter fun localDateToLong(value: LocalDate?): Long? = value?.toEpochDay()

    @TypeConverter fun longToLocalDate(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)

    @TypeConverter fun instantToLong(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter fun longToInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter fun dayStatusToString(value: DayStatus): String = value.name

    @TypeConverter fun stringToDayStatus(value: String): DayStatus = DayStatus.valueOf(value)

    @TypeConverter fun priorityToString(value: Priority): String = value.name

    @TypeConverter fun stringToPriority(value: String): Priority = Priority.valueOf(value)

    @TypeConverter fun projectStatusToString(value: ProjectStatus): String = value.name

    @TypeConverter fun stringToProjectStatus(value: String): ProjectStatus = ProjectStatus.valueOf(value)

    @TypeConverter fun creditDirectionToString(value: CreditDirection): String = value.name

    @TypeConverter fun stringToCreditDirection(value: String): CreditDirection = CreditDirection.valueOf(value)

    @TypeConverter fun noteCategoryToString(value: NoteCategory): String = value.name

    @TypeConverter fun stringToNoteCategory(value: String): NoteCategory = NoteCategory.valueOf(value)
}
