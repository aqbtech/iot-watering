package com.se.iotwatering.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PaginationUtils {
	public static <T> Page<T> convertListToPage(List<T> list, Pageable pageable) {
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<T> pageItems;
		if (list.size() < startItem) {
			pageItems = List.of();
		} else {
			int toIndex = Math.min(startItem + pageSize, list.size());
			pageItems = list.subList(startItem, toIndex);
		}
		return new PageImpl<>(pageItems, PageRequest.of(currentPage, pageSize), list.size());
	}
	public static <T> Page<T> convertListToPage(List<T> list, Pageable pageable, int total) {
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<T> pageItems;
		if (list.size() < startItem) {
			pageItems = List.of();
		} else {
			int toIndex = Math.min(startItem + pageSize, list.size());
			pageItems = list.subList(startItem, toIndex);
		}
		return new PageImpl<>(pageItems, PageRequest.of(currentPage, pageSize), total);
	}
}
