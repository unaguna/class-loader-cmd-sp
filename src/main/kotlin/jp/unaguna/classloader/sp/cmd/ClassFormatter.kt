package jp.unaguna.classloader.sp.cmd

import jp.unaguna.classloader.core.ScannedElement

class ClassFormatter(
    private val longFormat: Boolean = false,
    private val fieldSep: String = "\t"
) {
    fun format(scanned: ScannedElement<*>): String {
        return buildString {
            if (longFormat) {
                append(status(scanned))
                append(fieldSep)
            }

            append("\t".repeat(scanned.depth))
            append(scanned.className)
        }
    }

    private fun status(scanned: ScannedElement<*>): String {
        return buildString {
            append(when {
                // TODO: isInterface -> "i"
                scanned.isAbstract -> "a"
                else -> "c"
            })
        }
    }
}
