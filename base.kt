package bon

import bon.io.Json
import bon.io.Yaml

typealias Void = Unit

// Iterable  ----------------------------------------------------------------------------------------
fun <V, R> Iterable<V>.map(transform: (V, Int) -> R): List<R> =
  this.mapIndexed { i, v -> transform(v, i) }

operator fun <V> List<V>.get(range: IntRange): List<V> = this.slice(range)
operator fun <V> List<V>.get(indices: Set<Int>): List<V> = this.slice(indices)

inline fun <V> Iterable<V>.each(action: (V) -> Void): Void = this.forEach(action)

fun <V> Iterable<V>.reverse(): List<V> = this.reversed()

fun <V> Iterable<V>.sort_by(s1: (V) -> Comparable<*>, s2: (V) -> Comparable<*>, s3: (V) -> Comparable<*>) =
  sort_iterable_by(this, s1, s2, s3)
fun <V> Iterable<V>.sort_by(s1: (V) -> Comparable<*>, s2: (V) -> Comparable<*>) =
  sort_iterable_by(this, s1, s2)
fun <V> Iterable<V>.sort_by(s1: (V) -> Comparable<*>) = sort_iterable_by(this, s1)

fun <V> sort_iterable_by(iterable: Iterable<V>, vararg selectors: (V) -> Comparable<*>): List<V> {
  if (selectors.is_empty()) throw Exception("at least one comparator required")
  return iterable.sortedWith(compareBy(*selectors))
}

fun Iterable<*>.is_empty() = !this.iterator().hasNext()
fun Array<*>.is_empty() = size == 0


// List --------------------------------------------------------------------------------------------
fun <V> list_of(vararg array: V): List<V> = listOf(*array)
fun <V> list_of(iterable: Iterable<V>): List<V> = iterable.toList()

fun <V> Iterable<V>.to_list() = list_of(this)


// MutableList -------------------------------------------------------------------------------------
fun <V> mutable_list_of(vararg array: V): MutableList<V> = mutableListOf(*array)
fun <V> mutable_list_of(iterable: Iterable<V>): MutableList<V> = iterable.toMutableList()

fun <V> MutableList<V>.add_all(iterable: Iterable<V>): Boolean = this.addAll(iterable)


// Set ---------------------------------------------------------------------------------------------
fun <V> set_of(vararg array: V) = setOf(*array)
fun <V> set_of(iterable: Iterable<V>) = iterable.toSet()

fun <V> Iterable<V>.to_set() = this.toSet()


// MutableSet -------------------------------------------------------------------------------------
fun <V> mutable_set_of(vararg array: V): MutableSet<V> = mutableSetOf(*array)
fun <V> mutable_set_of(iterable: Iterable<V>): MutableSet<V> = iterable.toMutableSet()


// Dict --------------------------------------------------------------------------------------------
class Dict<K, V>(private val map: Map<K, V>) : Map<K, V> by map
//  , Iterable<Map.Entry<K, V>>
{
  operator fun get(key: K, default: V): V = map[key] ?: default

//  override fun iterator() = map.entries.iterator()
}

fun <K, V> dict_of(vararg pairs: Pair<K, V>) = Dict(mapOf(*pairs))
fun <K, V> dict_of(map: Map<K, V>) = Dict(map)


// MutableDict ------------------------------------------------------------------------------------
class MutableDict<K, V>(
  private val map: MutableMap<K, V>
) : MutableMap<K, V> by map
//  , Iterable<Map.Entry<K, V>>
{
//  override operator fun get(key: K): V = map[key].assert_not_null {"key required $key" }

  fun get_or_put(key: K, default: (key: K) -> V): V = map.getOrPut(key) { default(key) }

//  override fun iterator() = map.entries.iterator()
}

fun <K, V> mutable_dict_of(vararg pairs: Pair<K, V>) = MutableDict(mutableMapOf(*pairs))
fun <K, V> mutable_dict_of(map: MutableMap<K, V>) = MutableDict(map)


// Errorneous --------------------------------------------------------------------------------------
sealed class Errorneous<R>
data class Success<R>(val result: R) : Errorneous<R>()
data class Fail<R>(val error: Exception) : Errorneous<R>()

// JSON and YAML -----------------------------------------------------------------------------------
fun Any.to_json(pretty: Boolean = true) = Json.to_json(this, pretty)
fun from_json(json: String) = Json.from_json(json)

fun Any.to_yaml() = Yaml.to_yaml(this)


// Any --------------------------------------------------------------------------------------------
fun Any.hash_code() = this.hashCode()
fun Any.to_string() = this.toString()


fun <T> T?.assert_not_null(message: () -> String = { "not null required" }): T =
  this ?: throw Exception(message())

fun <T> p(a: T): Void = println(a)


//object BaseConfig {
//  val test = (get_env("test", "false").toLowerCase() == "true"
//}
//
//fun test(name: String, test: () -> Void) {
//  try {
//    if (BaseConfig.test) test()
//  } catch (e: Exception) {
//    log_error("'$name' failed", null)
//    throw e
//  }
//}


// timer -------------------------------------------------------------------------------------------
fun timer(start_ms: Long = System.currentTimeMillis()): () -> Long =
  { System.currentTimeMillis() - start_ms }

// assert ------------------------------------------------------------------------------------------
// Kotlin `assert` isn't enabled by default and requires `-ea` runtime JVM option.
fun assert(condition: Boolean, message: (() -> String) = { "Assert failed" }): Void {
  if (!condition) throw Exception(message())
}

// Constants ---------------------------------------------------------------------------------------
val min_ms  = 60 * 1000
val hour_ms = 60 * min_ms
val day_ms  = 24 * hour_ms