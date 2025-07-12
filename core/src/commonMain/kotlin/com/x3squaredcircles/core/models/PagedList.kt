// core/src/commonMain/kotlin/com/x3squaredcircles/core/models/PagedList.kt
package com.x3squaredcircles.core.models

import kotlinx.serialization.Serializable

/**
 * Represents a paged list of items with pagination metadata.
 * Used for efficient loading of large datasets.
 */
@Serializable
data class PagedList<T>(
    val items: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalCount: Int
) {
    /**
     * Gets the total number of pages.
     */
    val totalPages: Int = if (totalCount == 0) 0 else (totalCount + pageSize - 1) / pageSize
    
    /**
     * Indicates whether there is a previous page.
     */
    val hasPreviousPage: Boolean = pageNumber > 1
    
    /**
     * Indicates whether there is a next page.
     */
    val hasNextPage: Boolean = pageNumber < totalPages
    
    /**
     * Gets the starting item number for the current page (1-based).
     */
    val startingItemNumber: Int = if (items.isEmpty()) 0 else (pageNumber - 1) * pageSize + 1
    
    /**
     * Gets the ending item number for the current page (1-based).
     */
    val endingItemNumber: Int = if (items.isEmpty()) 0 else startingItemNumber + items.size - 1
    
    /**
     * Gets a summary string describing the current page (e.g., "Showing 1-10 of 25").
     */
    val pagingSummary: String
        get() = if (totalCount == 0) {
            "No items found"
        } else {
            "Showing $startingItemNumber-$endingItemNumber of $totalCount"
        }
    
    /**
     * Checks if this is the first page.
     */
    val isFirstPage: Boolean = pageNumber == 1
    
    /**
     * Checks if this is the last page.
     */
    val isLastPage: Boolean = pageNumber == totalPages
    
    /**
     * Checks if the list is empty.
     */
    val isEmpty: Boolean = items.isEmpty()
    
    /**
     * Maps the items to a different type while preserving pagination metadata.
     */
    fun <R> map(transform: (T) -> R): PagedList<R> {
        return PagedList(
            items = items.map(transform),
            pageNumber = pageNumber,
            pageSize = pageSize,
            totalCount = totalCount
        )
    }
    
    companion object {
        /**
         * Creates an empty paged list.
         */
        fun <T> empty(pageNumber: Int = 1, pageSize: Int = 10): PagedList<T> {
            return PagedList(
                items = emptyList(),
                pageNumber = pageNumber,
                pageSize = pageSize,
                totalCount = 0
            )
        }
        
        /**
         * Creates a paged list from a full list by taking a subset.
         */
        fun <T> fromList(
            fullList: List<T>,
            pageNumber: Int,
            pageSize: Int
        ): PagedList<T> {
            val startIndex = (pageNumber - 1) * pageSize
            val endIndex = kotlin.math.min(startIndex + pageSize, fullList.size)
            
            val pageItems = if (startIndex < fullList.size) {
                fullList.subList(startIndex, endIndex)
            } else {
                emptyList()
            }
            
            return PagedList(
                items = pageItems,
                pageNumber = pageNumber,
                pageSize = pageSize,
                totalCount = fullList.size
            )
        }
    }
}