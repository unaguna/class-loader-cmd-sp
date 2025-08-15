package jp.unaguna.classloader.sp.cmd

import jp.unaguna.classloader.core.ScannedElement

interface ClassMetaStatusFormatter {
    fun format(scanned: ScannedElement<*>): String
}

class ClassMetaStatusFormatterNone: ClassMetaStatusFormatter {
    override fun format(scanned: ScannedElement<*>): String {
        return ""
    }
}

class ClassMetaStatusFormatterShort(
    private val suffix: String = "",
): ClassMetaStatusFormatter {
    override fun format(scanned: ScannedElement<*>): String {
        return buildString {
            append(when {
                scanned.isInterface -> "i"
                scanned.isAbstract -> "a"
                else -> "c"
            })
            append(suffix)
        }
    }
}
