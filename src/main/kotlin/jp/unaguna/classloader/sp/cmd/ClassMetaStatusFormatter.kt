package jp.unaguna.classloader.sp.cmd

import jp.unaguna.classloader.core.ScannedElement
import jp.unaguna.classloader.core.Visibility

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
                scanned.isAnnotation -> "@"
                scanned.isInterface -> "i"
                scanned.isAbstract -> "a"
                else -> "c"
            })
            append(when (scanned.visibility) {
                Visibility.PUBLIC -> "P"
                Visibility.PRIVATE -> "p"
                Visibility.PROTECTED -> "r"
                Visibility.PACKAGE_PRIVATE -> "-"
            })
            append(when {
                scanned.isFinal -> "f"
                else -> "-"
            })
            append(suffix)
        }
    }
}

class ClassMetaStatusFormatterLong(
    private val sep: String = " ",
    private val suffix: String = "",
): ClassMetaStatusFormatter {
    override fun format(scanned: ScannedElement<*>): String {
        return buildString {
            append(when {
                scanned.isAnnotation -> "annotation"
                scanned.isInterface ->  "interface "
                scanned.isAbstract ->   "abstract  "
                else ->                 "concrete  "
            })
            append(sep)
            append(when (scanned.visibility) {
                Visibility.PUBLIC ->          "public   "
                Visibility.PRIVATE ->         "private  "
                Visibility.PROTECTED ->       "protected"
                Visibility.PACKAGE_PRIVATE -> "package  "
            })
            append(sep)
            append(when {
                scanned.isFinal -> "final"
                else ->            "open "
            })
            append(suffix)
        }
    }
}
