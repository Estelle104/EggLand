package com.app.eggland.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class PaginationUtils {
    public static <T> Page<T> paginerListe(List<T> listeComplete, int page, int size) {
        if (listeComplete == null) {
            listeComplete = new ArrayList<>();
        }

        int totalElements = listeComplete.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        if (totalPages == 0) totalPages = 1; 

        // Ta fameuse sécurité anti-écran blanc
        if (page >= totalPages) {
            page = totalPages - 1;
        }
        if (page < 0) {
            page = 0;
        }

        // Le découpage (subList)
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<T> subList = listeComplete.subList(fromIndex, toIndex);

        // On emballe le tout dans un objet Page officiel de Spring
        return new PageImpl<>(subList, PageRequest.of(page, size), totalElements);
    }
}
