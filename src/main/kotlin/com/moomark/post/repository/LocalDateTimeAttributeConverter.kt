package com.moomark.post.repository

import java.sql.Timestamp
import java.time.LocalDateTime
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class LocalDateTimeAttributeConverter : AttributeConverter<LocalDateTime, Timestamp> {
    override fun convertToDatabaseColumn(locDateTime: LocalDateTime?): Timestamp? =
        locDateTime?.let { Timestamp.valueOf(it) }

    override fun convertToEntityAttribute(sqlTimestamp: Timestamp?): LocalDateTime? = sqlTimestamp?.toLocalDateTime()
}
