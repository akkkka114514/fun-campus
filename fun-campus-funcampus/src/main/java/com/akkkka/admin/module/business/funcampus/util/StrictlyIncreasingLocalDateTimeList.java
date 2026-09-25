package com.akkkka.admin.module.business.funcampus.util;

/**
 * author:akkkka114514
 * create at 2026-02-25 15:32
 * 严格递增，插入元素不符合会报错
 */
import java.time.LocalDateTime;
import java.util.*;

public class StrictlyIncreasingLocalDateTimeList {
    private final List<LocalDateTime> data = new ArrayList<>();

    public void add(LocalDateTime element) {
        if (!data.isEmpty()) {
            LocalDateTime last = data.get(data.size() - 1);
            if (!element.isAfter(last)) {
                throw new IllegalArgumentException(
                        "元素必须严格大于前一个元素: " + element + " <= " + last
                );
            }
        }
        data.add(element);
    }

    public LocalDateTime get(int index) {
        return data.get(index);
    }

    public int size() {
        return data.size();
    }

    @Override
    public String toString() {
        return "StrictlyIncreasingList" + data;
    }

}