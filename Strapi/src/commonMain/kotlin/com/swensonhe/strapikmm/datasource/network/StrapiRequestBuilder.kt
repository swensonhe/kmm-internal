package com.swensonhe.strapikmm.datasource.network

import io.ktor.http.*

class StrapiRequestBuilder {
    private lateinit var requestEndpoint: String
    private val contents: MutableList<RequestContent> = mutableListOf()
    private var queryBuilder: StrapiQueryBuilder? = null

    fun endpoint(endpoint: String) {
        this.requestEndpoint = endpoint
    }

    fun query(key: String, value: String) = apply {
        contents.add(RequestContent.Query(key, value))
    }

    fun path(key: String, value: String) = apply {
        contents.add(RequestContent.Path(key, value))
    }

    fun <T> body(value: T) = apply {
        if(contents.any { it is RequestContent.Body<*> }) {
            throw IllegalStateException("You can pass only one body data inside the request")
        }

        header(HttpHeaders.ContentType, "application/json")
        contents.add(RequestContent.Body(value))
    }

    fun header(key: String, value: String) = apply {
        contents.add(RequestContent.Header(key, value))
    }

    fun strapiQueryBuilder(strapiQueryBuilder: StrapiQueryBuilder.() -> Unit = {}) = apply {
        val builder = StrapiQueryBuilder()
        builder.strapiQueryBuilder()
        queryBuilder = builder
    }

    fun build(): Pair<String, List<RequestContent>> {
        contents.addAll(queryBuilder?.extractQueries().orEmpty())

        val pathContents = contents.filterIsInstance<RequestContent.Path>()
        var updatedUrl = requestEndpoint

        pathContents.forEach {
            updatedUrl = updatedUrl.replace("{${it.key}}", it.value)
        }

        return updatedUrl to contents.filter { it !is RequestContent.Path }
    }

}

class StrapiQueryBuilder {

    var filters: MutableMap<String, MutableList<String>> = mutableMapOf()

    fun add(field: String, value: String) = apply {
        put(field, value)
    }

    fun add(field: String, value: MutableList<String>) = apply {
        put(field, value)
    }

    fun add(map: Map<String, MutableList<String>>) = apply {
        map.forEach { item ->
            put(item.key, item.value)
        }
    }

    fun populate(key: String) = apply {
        put("populate", key)
    }
    fun groupBy(key: String) = apply {
        put("groupBy", key)
    }

    fun equalTo(field: String, value: String) = apply {
        val filterQuery = field.split(".").map { "[$it]" }
        put("filters[${filterQuery}][\$eq]", value)
    }

    fun notEqualTo(field: String, value: String) = apply {
        val filterQuery = field.split(".").map { "[$it]" }
        put("filters[${filterQuery}][\$ne]", value)
    }

    fun lessThan(field: String, value: String) = apply {
        val filterQuery = field.split(".").map { "[$it]" }
        put("filters[${filterQuery}][\$lt]", value)
    }

    fun greaterThan(field: String, value: String) = apply {
        val filterQuery = field.split(".").map { "[$it]" }
        put("filters[${filterQuery}][\$gt]", value)
    }

    fun lessThanOrEqual(field: String, value: String) = apply {
        val filterQuery = field.split(".").map { "[$it]" }
        put("filters[${filterQuery}][\$lte]", value)
    }

    fun greaterThanOrEqual(field: String, value: String) = apply {
        val filterQuery = field.split(".").map { "[$it]" }
        put("filters[${filterQuery}][\$gte]", value)
    }

    fun includedIn(field: String, value: List<String>) = apply {
        val filterQuery = field.split(".").map { "[$it]" }
        value.forEach {
            put("filters[${filterQuery}][\$in]", it)
        }
    }

    fun noIncludedIn(field: String, value: List<String>) = apply {
        val filterQuery = field.split(".").map { "[$it]" }
        value.forEach {
            put("filters[${filterQuery}][\$nin]", it)
        }
    }

    fun contains(field: String, value: List<String>) = apply {
        val filterQuery = field.split(".").map { "[$it]" }
        value.forEach {
            put("filters[${filterQuery}][\$contains]", it)
        }
    }

    fun notContains(field: String, value: List<String>) = apply {
        val filterQuery = field.split(".").map { "[$it]" }
        value.forEach {
            put("filters[${filterQuery}][\$ncontain]", it)
        }
    }

    fun containsCaseSensitive(field: String, value: List<String>) = apply {
        val filterQuery = field.split(".").map { "[$it]" }
        value.forEach {
            put("filters[${filterQuery}][\$containss]", it)
        }
    }

    fun notContainsCaseSensitive(field: String, value: List<String>) = apply {
        val filterQuery = field.split(".").map { "[$it]" }
        value.forEach {
            put("filters[${filterQuery}][\$ncontainss]", it)
        }
    }

    fun sortBy(vale: String, type: StrapiSortType) {
        put("sort", "$vale${type.type}")
    }

    fun paging(page: Int, pageSize: Int) {
        put("pagination[page]", page.toString())
        put("pagination[pageSize]", pageSize.toString())
        put("pagination[withCount]", true.toString())
    }

    private fun put(key: String, value: String) {
        if (filters[key] == null) {
            filters[key] = mutableListOf()
        }
        filters[key]?.add(value)
    }

    private fun put(key: String, value: MutableList<String>) {
        if (filters[key] == null) {
            filters[key] = mutableListOf()
        }
        filters[key]?.addAll(value)
    }
}

enum class StrapiSortType(val type: String) {
    ASC(":asc"),
    DESC(":desc"),
}

sealed class RequestContent {
    class Query(val key: String, val value: String) : RequestContent()
    class Path(val key: String, val value: String) : RequestContent()
    class Header(val key: String, val value: String) : RequestContent()
    class Body<T>(val value: T) : RequestContent()
}

fun StrapiQueryBuilder.extractQueries(): List<RequestContent.Query> {
    return this.filters.map { entry ->
        return@map entry.value.map { entryValue ->
            RequestContent.Query(entry.key, entryValue)
        }
    }.flatten()
}