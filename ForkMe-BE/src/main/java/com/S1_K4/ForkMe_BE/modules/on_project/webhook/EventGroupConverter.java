package com.S1_K4.ForkMe_BE.modules.on_project.webhook;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.webhook
 * @fileName : EventGroupConverter
 * @date : 2025-08-20
 * @description : webhook enumtype 변경 컨버터입니다
 */
@Converter(autoApply = false)
public class EventGroupConverter implements AttributeConverter<EventGroup, String> {

    private static final Logger log = LoggerFactory.getLogger(EventGroupConverter.class);

    @Override
    public String convertToDatabaseColumn(EventGroup attribute) {
        if (attribute == null) return null;
        // Normalize: store enum name as string
        return attribute.name();
    }

    @Override
    public EventGroup convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        String trimmed = dbData.trim();

        // 1) numeric ordinal 처리 시도
        try {
            int ordinal = Integer.parseInt(trimmed);
            EventGroup[] vals = EventGroup.values();
            if (ordinal >= 0 && ordinal < vals.length) {
                return vals[ordinal];
            } else {
                log.warn("EventGroupConverter: ordinal out of range: {} (db='{}')", ordinal, dbData);
            }
        } catch (NumberFormatException ignored) {
            // 숫자 아님 -> 다음 처리
        }

        // 2) enum 이름으로 처리 (대소문자 무시)
        try {
            return EventGroup.valueOf(trimmed.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            log.warn("EventGroupConverter: unknown enum name '{}' -> defaulting to OTHERS", dbData);
        }

        // 3) 안전한 기본값
        return EventGroup.OTHERS;
    }
}
