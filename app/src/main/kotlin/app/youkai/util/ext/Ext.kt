package app.youkai.util.ext

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import java.text.NumberFormat
import java.util.*

/**
 * Various extension functions for various purposes that don't really belong in a specific file.
 */

/**
 * Hack to make it possible to use functions with return types in places where Unit is expected.
 */
fun Any.unitify() {
    /* do nothing */
}

/**
 * Convenience function for getting [LayoutInflater]
 */
fun Context.getLayoutInflater(): LayoutInflater {
    return getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
}

/**
 * Assumes this [Int] is in dp units and converts it into pixels.
 */
fun Int.toPx(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}

/**
 * Assumes this [Int] is in pixel units and converts it into dp units.
 */
fun Int.toDp(context: Context): Int {
    return (this / context.resources.displayMetrics.density).toInt()
}

/**
 * Returns a View visibility int from this boolean.
 */
fun Boolean.toVisibility(): Int {
    return if (this) View.VISIBLE else View.GONE
}

/**
 * Appends a string to a string.
 */
fun String.append(other: String, delimiter: String = ""): String {
    return if (!isEmpty()) plus(delimiter).plus(other) else other
}

/**
 * Appends a string to an entry in a MutableMap.
 */
fun MutableMap<String, String>.append(key: String, other: String, delimiter: String = ""): MutableMap<String, String> {
    put(key, (this[key] ?: "").append(other, delimiter))
    return this
}

/**
 * Returns the input as a string.
 */
fun EditText.inputString(): String = text.toString()

/**
 * Formats itself with appropriate commas and dots and returns the resulting String.
 *
 * Currently uses US locale since the app is only available in English (US). We will have to change
 * this behavior if we ever decide to translate the app.
 */
fun Number.formatForDisplay(): String {
    return NumberFormat.getInstance(Locale.US).format(this)
}

/**
 * Formats itself by rounding the decimals to two digits and returns the resulting String.
 *
 * Currently uses US locale since the app is only available in English (US). We will have to change
 * this behavior if we ever decide to translate the app.
 */
fun Number.formatTwoDecimals(): String {
    return String.format(Locale.US, "%.2f", this)
}

/**
  * Capitalizes the first letter of the string.
  */
fun String.capitalizeFirstLetter(): String {
    return this.first().toString().capitalize().append(this.substring(1))
}