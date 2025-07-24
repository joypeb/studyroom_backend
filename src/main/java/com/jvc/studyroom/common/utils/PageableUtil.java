package com.jvc.studyroom.common.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class PageableUtil {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final String DEFAULT_SORT_DIRECTION = "asc";

    /**
     * 기본 Pageable 생성
     */
    public Pageable createPageable(int page, int size) {
        return PageRequest.of(
            validatePage(page),
            validateSize(size)
        );
    }

    /**
     * 정렬이 포함된 Pageable 생성
     */
    public Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        Sort sort = createSort(sortBy, sortDirection);
        return PageRequest.of(
            validatePage(page),
            validateSize(size),
            sort
        );
    }

    /**
     * 복합 정렬이 포함된 Pageable 생성
     */
    public Pageable createPageable(int page, int size, List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return createPageable(page, size);
        }

        List<Sort.Order> orders = sortParams.stream()
            .map(this::parseSort)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        Sort sort = orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);

        return PageRequest.of(
            validatePage(page),
            validateSize(size),
            sort
        );
    }

    /**
     * Map 형태의 파라미터로부터 Pageable 생성
     */
    public Pageable createPageable(Map<String, Object> params) {
        int page = getIntParam(params, "page", DEFAULT_PAGE);
        int size = getIntParam(params, "size", DEFAULT_SIZE);
        String sortBy = getStringParam(params, "sortBy", null);
        String sortDirection = getStringParam(params, "sortDirection", DEFAULT_SORT_DIRECTION);

        if (sortBy != null && !sortBy.trim().isEmpty()) {
            return createPageable(page, size, sortBy, sortDirection);
        }

        return createPageable(page, size);
    }

    /**
     * Page 응답 생성 (기본)
     */
    public <T> Mono<Page<T>> createPageResponse(
            Flux<T> dataFlux,
            Mono<Long> countMono,
            Pageable pageable) {

        return dataFlux.collectList()
            .zipWith(countMono)
            .map(tuple -> createPage(tuple.getT1(), pageable, tuple.getT2()))
            .doOnSuccess(page -> log.debug("Page created: {} elements, page {}/{}",
                page.getNumberOfElements(), page.getNumber(), page.getTotalPages()))
            .onErrorResume(error -> {
                log.error("Error creating page response", error);
                return Mono.just(createEmptyPage(pageable));
            });
    }

    private <T> Page<T> createPage(List<T> content, Pageable pageable, Long total) {
        return new PageImpl<>(content, pageable, total);
    }

    private <T> Page<T> createEmptyPage(Pageable pageable) {
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    /**
     * 커스텀 응답 형태로 Page 생성
     */
    public <T> Mono<ResponseEntity<Map<String, Object>>> createCustomPageResponse(
            Flux<T> dataFlux,
            Mono<Long> countMono,
            Pageable pageable,
            String dataKey) {

        return dataFlux.collectList()
            .zipWith(countMono)
            .map(tuple -> {
                List<T> data = tuple.getT1();
                Long totalCount = tuple.getT2();

                Map<String, Object> response = new HashMap<>();
                response.put(dataKey, data);
                response.put("pagination", createPaginationInfo(pageable, totalCount, data.size()));

                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                log.error("Error creating custom page response", error);
                Map<String, Object> errorResponse = Map.of(
                    "error", "데이터 조회 중 오류가 발생했습니다.",
                    "message", error.getMessage()
                );
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse));
            });
    }

    /**
     * 검색 조건이 있는 경우의 Page 응답 생성
     */
    public <T> Mono<Page<T>> createSearchPageResponse(
            Supplier<Flux<T>> dataSupplier,
            Supplier<Mono<Long>> countSupplier,
            Pageable pageable) {

        return Mono.fromSupplier(dataSupplier)
            .flatMap(Flux::collectList)
            .zipWith(Mono.fromSupplier(countSupplier).flatMap(Function.identity()))
            .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    /**
     * 페이징 정보 생성
     */
    public Map<String, Object> createPaginationInfo(Pageable pageable, Long totalElements, int currentElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", pageable.getPageNumber());
        pagination.put("pageSize", pageable.getPageSize());
        pagination.put("totalElements", totalElements);
        pagination.put("totalPages", totalPages);
        pagination.put("currentElements", currentElements);
        pagination.put("hasNext", pageable.getPageNumber() < totalPages - 1);
        pagination.put("hasPrevious", pageable.getPageNumber() > 0);
        pagination.put("first", pageable.getPageNumber() == 0);
        pagination.put("last", pageable.getPageNumber() >= totalPages - 1);

        return pagination;
    }

    /**
     * 빈 Page 응답 생성
     */
    public <T> Mono<Page<T>> createEmptyPageResponse(Pageable pageable) {
        return Mono.just(new PageImpl<>(Collections.emptyList(), pageable, 0));
    }

    /**
     * 빈 커스텀 응답 생성
     */
    public Mono<ResponseEntity<Map<String, Object>>> createEmptyCustomResponse(
            Pageable pageable, String dataKey) {
        Map<String, Object> response = new HashMap<>();
        response.put(dataKey, Collections.emptyList());
        response.put("pagination", createPaginationInfo(pageable, 0L, 0));

        return Mono.just(ResponseEntity.ok(response));
    }

    // === Private Helper Methods ===

    private int validatePage(int page) {
        return Math.max(0, page);
    }

    private int validateSize(int size) {
        return Math.min(Math.max(1, size), MAX_SIZE);
    }

    private Sort createSort(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return Sort.unsorted();
        }

        return sortDirection != null && sortDirection.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() :
            Sort.by(sortBy).ascending();
    }

    private Sort.Order parseSort(String sortStr) {
        try {
            String[] parts = sortStr.split(",");
            String property = parts[0].trim();

            if (property.isEmpty()) {
                return null;
            }

            String direction = parts.length > 1 ? parts[1].trim() : DEFAULT_SORT_DIRECTION;

            return direction.equalsIgnoreCase("desc") ?
                Sort.Order.desc(property) :
                Sort.Order.asc(property);
        } catch (Exception e) {
            log.warn("Invalid sort parameter: {}", sortStr);
            return null;
        }
    }

    private int getIntParam(Map<String, Object> params, String key, int defaultValue) {
        Object value = params.get(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            if (value instanceof String) {
                return Integer.parseInt((String) value);
            } else if (value instanceof Number) {
                return ((Number) value).intValue();
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid integer parameter {}: {}", key, value);
        }

        return defaultValue;
    }

    private String getStringParam(Map<String, Object> params, String key, String defaultValue) {
        Object value = params.get(key);
        return value != null ? value.toString() : defaultValue;
    }
}
