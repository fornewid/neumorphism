package soup.neumorphism.internal.util

import java.lang.ref.SoftReference
import java.util.AbstractMap

internal class SoftHashMap<K, V>(
    private val maxSize: Int? = null
) : Map<K, V?> {

    private val linkedHashMap = object : LinkedHashMap<K, SoftReference<V>>() {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, SoftReference<V>>?) =
            maxSize != null && size > maxSize
    }

    override val entries: Set<Map.Entry<K, V?>>
        get() = linkedHashMap.entries.map { entry ->
            AbstractMap.SimpleEntry(entry.key, entry.value.get())
        }.toSet()

    override val keys: Set<K>
        get() = linkedHashMap.keys

    override val size: Int
        get() = linkedHashMap.size

    override val values: Collection<V?>
        get() = linkedHashMap.values.map { softReference ->
            softReference.get()
        }

    fun put(key: K, value: V) = linkedHashMap.put(key, SoftReference(value))

    override fun containsKey(key: K): Boolean
        = linkedHashMap.containsKey(key)

    override fun containsValue(value: V?): Boolean
        = linkedHashMap.containsValue(SoftReference(value))

    override operator fun get(key: K): V?
        = linkedHashMap[key]?.get()

    override fun isEmpty(): Boolean
        = linkedHashMap.isEmpty()
}