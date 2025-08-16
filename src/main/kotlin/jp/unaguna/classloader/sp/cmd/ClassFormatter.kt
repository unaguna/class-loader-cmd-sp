package jp.unaguna.classloader.sp.cmd

import jp.unaguna.classloader.core.ScannedElement

class ClassFormatter(
    private val longFormat: Boolean = false,
    longStatus: Boolean = false,
    private val showSource: Boolean = false,
    private val fieldSep: String = " "
) {
    private val statusFormatter = when {
        longFormat && longStatus -> ClassMetaStatusFormatterLong(
            suffix = fieldSep,
        )
        longFormat -> ClassMetaStatusFormatterShort(
            suffix = fieldSep,
        )
        else -> ClassMetaStatusFormatterNone()
    }

    fun format(scanned: ScannedElement<*>): String {
        return buildString {
            append(statusFormatter.format(scanned))

            if (longFormat) {
                append(scanned.major)
                append(" ")
                append(scanned.minor)
                append(fieldSep)
            }

            append("\t".repeat(scanned.depth))
            append(scanned.className)

            if (showSource) {
                append(fieldSep)
                append(scanned.classSource)
            }
        }
    }
}
