package jp.unaguna.classloader.sp.cmd

import jp.unaguna.classloader.core.ScannedElement

class ClassFormatter(
    longFormat: Boolean = false,
    fieldSep: String = "\t"
) {
    private val statusFormatter = when {
        longFormat -> ClassMetaStatusFormatterShort(
            suffix = fieldSep,
        )
        else -> ClassMetaStatusFormatterNone()
    }

    fun format(scanned: ScannedElement<*>): String {
        return buildString {
            append(statusFormatter.format(scanned))

            append("\t".repeat(scanned.depth))
            append(scanned.className)
        }
    }
}
