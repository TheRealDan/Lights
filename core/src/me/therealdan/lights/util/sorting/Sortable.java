package me.therealdan.lights.util.sorting;

import java.util.ArrayList;
import java.util.List;

public interface Sortable {

    default boolean isBefore(Sortable sortable, Sort sort, Order order) {
        switch (order) {
            case ASCENDING:
                return isAfter(sortable, sort);
            case DESCENDING:
                return isBefore(sortable, sort);
        }
        return false;
    }

    default boolean isAfter(Sortable sortable, Sort sort, Order order) {
        switch (order) {
            case ASCENDING:
                return isBefore(sortable, sort);
            case DESCENDING:
                return isAfter(sortable, sort);
        }
        return false;
    }

    default boolean isBefore(Sortable sortable, Sort sort) {
        switch (sort) {
            case NAME:
                return getSortName().compareTo(sortable.getSortName()) > 0;
            case ID:
                return getID() < sortable.getID();
            case POSITION:
                return getPosition() < sortable.getPosition();
        }
        return false;
    }

    default boolean isAfter(Sortable sortable, Sort sort) {
        switch (sort) {
            case NAME:
                return getSortName().compareTo(sortable.getSortName()) < 0;
            case ID:
                return getID() > sortable.getID();
            case POSITION:
                return getPosition() > sortable.getPosition();
        }
        return false;
    }

    default String getSortName() {
        return getName().toLowerCase();
    }

    default String getName() {
        return "";
    }

    default int getID() {
        return 0;
    }

    default int getPosition() {
        return 0;
    }

    enum Sort {
        NAME,
        ID,
        POSITION,
    }

    enum Order {
        ASCENDING,
        DESCENDING,
    }

    static List<Sortable> sort(List<Sortable> sortables, Sort... sort) {
        return sort(sortables, Order.ASCENDING, sort);
    }

    static List<Sortable> sort(List<Sortable> sortables, Order order, Sort... sort) {
        List<Sortable> sorted = new ArrayList<>();

        while (sortables.size() > 0) {
            Sortable next = null;
            for (Sortable sortable : sortables) {
                if (next == null) {
                    next = sortable;
                } else {
                    sort:
                    for (Sort by : sort) {
                        if (sortable.isBefore(next, by, order)) {
                            next = sortable;
                        } else if (sortable.isAfter(next, by, order)) {
                            break;
                        } else {
                            break sort;
                        }
                    }
                }
            }
            sorted.add(next);
            sortables.remove(next);
        }

        return sorted;
    }
}