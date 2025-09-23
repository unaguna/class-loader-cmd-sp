package jp.unaguna.classloader.sp.cmd

import jp.unaguna.classloader.core.Visibility
import jp.unaguna.classloader.sp.SpringClasspathScannerElement
import jp.unaguna.fmtbuilder.DataFormat
import jp.unaguna.fmtbuilder.ValuePadding
import jp.unaguna.fmtbuilder.ValueProviderAdapter
import jp.unaguna.fmtbuilder.VariablePaddingSpecifications

private val padding = VariablePaddingSpecifications().apply {
    add("%m", ValuePadding.LEFT)
    add("%M", ValuePadding.LEFT)
}

fun createClassLineFormatter(
    longFormat: Boolean = false,
    longStatus: Boolean = false,
    showSource: Boolean = false,
    fieldSep: String = " "
): DataFormat {
    val format = buildString {
        when {
            longFormat && longStatus -> {
                append("%t")
                append(fieldSep)
                append("%v")
                append(fieldSep)
                append("%f")
                append(fieldSep)
            }
            longFormat -> {
                append("%s")
                append(fieldSep)
            }
        }

        if (longFormat) {
            append("%M.%m")
            append(fieldSep)
        }

        append("%E%c")

        if (showSource) {
            append(fieldSep)
            append("%r")
        }
    }
    return createClassLineFormatter(format)
}

fun createClassLineFormatter(
    format: String,
): DataFormat {
    return DataFormat.fromPrintfFormat(format, padding)
}

@Suppress("CyclomaticComplexMethod")
fun createClassValueProviderAdapter(): ValueProviderAdapter<SpringClasspathScannerElement> {
    return ValueProviderAdapter.Builder<SpringClasspathScannerElement>().apply {
        addProvider("%c") { it.className }
        addProvider("%C") { it.shortClassName }
        addProvider("%t") {
            when {
                it.isAnnotation -> "annotation"
                it.isInterface -> "interface "
                it.isAbstract -> "abstract  "
                else -> "concrete  "
            }
        }
        addProvider("%v") {
            when (it.visibility) {
                Visibility.PUBLIC -> "public   "
                Visibility.PRIVATE -> "private  "
                Visibility.PROTECTED -> "protected"
                Visibility.PACKAGE_PRIVATE -> "package  "
            }
        }
        addProvider("%f") {
            when {
                it.isFinal -> "final"
                else -> "open "
            }
        }
        addProvider("%s") {
            buildString {
                append(
                    when {
                        it.isAnnotation -> "@"
                        it.isInterface -> "i"
                        it.isAbstract -> "a"
                        else -> "c"
                    }
                )
                append(
                    when (it.visibility) {
                        Visibility.PUBLIC -> "P"
                        Visibility.PRIVATE -> "p"
                        Visibility.PROTECTED -> "r"
                        Visibility.PACKAGE_PRIVATE -> "-"
                    }
                )
                append(
                    when {
                        it.isFinal -> "f"
                        else -> "-"
                    }
                )
            }
        }
        addProvider("%M") { it.major }
        addProvider("%m") { it.minor }
        addProvider("%r") { it.classSource }
        addProvider("%E") { "\t".repeat(it.depth) }
    }.build()
}
