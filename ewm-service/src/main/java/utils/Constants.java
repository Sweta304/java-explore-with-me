package utils;

import org.springframework.data.domain.Sort;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final Sort SORT_BY_ID = Sort.by(Sort.Direction.DESC, "id");
}
