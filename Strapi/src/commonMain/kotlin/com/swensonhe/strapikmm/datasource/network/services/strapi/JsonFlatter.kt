package com.swensonhe.strapikmm.datasource.network.services.strapi

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import kotlin.reflect.typeOf

//TODO Handle descriptive errors while parsing
object JsonFlatter {

    inline fun <reified T> flat(jsonElement: JsonElement): JsonElement {
        val descriptor = serializer(typeOf<T>()).descriptor
        val elementNames = descriptor.elementNames

        return when(jsonElement) {
            is JsonObject -> {
                val map = mutableMapOf<String, JsonElement>()
                elementNames.forEachIndexed { index, elementName ->
                    val childDescriptor = descriptor.getElementDescriptor(index)
                    map[elementName] = parse(elementName, jsonElement, childDescriptor)
                }

                JsonObject(map)
            }
            is JsonArray -> {
                val jsonElements = jsonElement.mapIndexed { index, element ->
                    val childDescriptor = descriptor.getElementDescriptor(index)
                    parse(element, childDescriptor)
                }

                JsonArray(jsonElements)
            }
            else -> {
                throw IllegalStateException("Malformed JSON passed to parser, expected object or array but got $jsonElement")
            }
        }
    }

    @ExperimentalSerializationApi
    fun parse(json: JsonObject, descriptor: SerialDescriptor): JsonObject {
        val map = mutableMapOf<String, JsonElement>()
        val elementNames = descriptor.elementNames
        elementNames.forEachIndexed { index, elementName ->
            val childDescriptor = descriptor.getElementDescriptor(index)
            map[elementName] = parse(elementName, json, childDescriptor)
        }
        return JsonObject(map)
    }

    @ExperimentalSerializationApi
    fun parse(
        elementName: String,
        jsonObject: JsonObject,
        descriptor: SerialDescriptor
    ): JsonElement {
        if (elementName.contains(".")) {
            val serializedNameComponents = elementName.split(".")
            var jsonElement: JsonElement? = null
            serializedNameComponents.forEachIndexed { index, serializedNameComponent ->
                if (jsonElement == null) {
                    jsonElement = jsonObject.get(serializedNameComponent) ?: JsonNull
                } else {
                    if(jsonElement is JsonNull){
                        return JsonNull
                    } else if (index == serializedNameComponents.lastIndex) {
                        jsonElement =
                            jsonElement?.jsonObject?.get(serializedNameComponent) ?: JsonNull
                        return parse(jsonElement, descriptor)
                    } else {
                        jsonElement = jsonElement?.jsonObject?.get(serializedNameComponent)
                            ?: JsonNull
                    }
                }
            }
            return JsonNull
        } else {
            return parse(jsonObject[elementName], descriptor)
        }
    }

    @ExperimentalSerializationApi
    fun parse(
        jsonElement: JsonElement?,
        descriptor: SerialDescriptor
    ): JsonElement {
        return when (jsonElement) {
            is JsonObject -> {
                val jsonObjectValue = jsonElement.jsonObject
                parse(jsonObjectValue, descriptor)
            }
            is JsonArray -> {
                parse(jsonElement, descriptor)
            }
            is JsonPrimitive -> {
                jsonElement
            }
            else -> {
                jsonElement ?: JsonNull
            }
        }
    }

    @ExperimentalSerializationApi
    fun parse(
        jsonArray: JsonArray,
        descriptor: SerialDescriptor
    ): JsonElement{
        val data = jsonArray.mapIndexed{ index, jsonElement ->
            val childDescriptor = descriptor.getElementDescriptor(index)
            parse(jsonElement, childDescriptor)
        }

        return JsonArray(data)
    }
}
