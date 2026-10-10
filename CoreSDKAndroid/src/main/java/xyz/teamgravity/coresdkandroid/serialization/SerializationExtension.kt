package xyz.teamgravity.coresdkandroid.serialization

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.serializer
import kotlin.reflect.full.createType

fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is JsonElement -> this
    is Number -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Array<*> -> JsonArray(map { it.toJsonElement() })
    is List<*> -> JsonArray(map { it.toJsonElement() })
    is Map<*, *> -> JsonObject(map { it.key.toString() to it.value.toJsonElement() }.toMap())
    else -> Json.encodeToJsonElement(serializer(this::class.createType()), this)
}

/**
 * Converts JsonElement to real types with Any.
 */
val JsonElement.extractedContent: Any?
    get() {
        return when (this) {
            is JsonPrimitive -> {
                if (jsonPrimitive.isString) return jsonPrimitive.content
                return jsonPrimitive.booleanOrNull
                    ?: jsonPrimitive.intOrNull
                    ?: jsonPrimitive.longOrNull
                    ?: jsonPrimitive.floatOrNull
                    ?: jsonPrimitive.doubleOrNull
                    ?: jsonPrimitive.contentOrNull
            }

            is JsonArray -> jsonArray.map { it.extractedContent }
            is JsonObject -> jsonObject.entries.associate { it.key to it.value.extractedContent }
        }
    }

/**
 * Converts JsonObject values to real types of Any.
 */
fun JsonObject.toExtractedMap(): Map<String, Any?> {
    return mapValues { it.value.extractedContent }
}

/**
 * Converts Map<String, Any?> to JsonObject.
 */
fun Map<String, Any?>.toJsonObject(): JsonObject {
    return JsonObject(mapValues { it.value.toJsonElement() })
}