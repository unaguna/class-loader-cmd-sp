package jp.unaguna.classloader.sp.cmd

import jp.unaguna.classloader.sp.SpringClassReducerType
import jp.unaguna.classloader.sp.SpringPackageScannerElement
import jp.unaguna.fmtbuilder.DataFormat
import jp.unaguna.fmtbuilder.ValuePadding
import jp.unaguna.fmtbuilder.ValueProviderAdapter
import jp.unaguna.fmtbuilder.VariablePaddingSpecifications

private val padding = VariablePaddingSpecifications().apply {
    add("%c", ValuePadding.LEFT)
    add("%V", ValuePadding.LEFT)
}

fun createPackageLineFormatter(
    format: String?,
): DataFormat {
    return DataFormat.fromPrintfFormat(format ?: "%P", padding)
}

val packageLineFormatterVariableReducerMap = mapOf<String, SpringClassReducerType<*>>(
    "%c" to SpringClassReducerType.CountAll,
    "%V" to SpringClassReducerType.MaxJavaVersion,
)

@Suppress("CyclomaticComplexMethod")
fun createPackageValueProviderAdapter(): ValueProviderAdapter<SpringPackageScannerElement> {
    return ValueProviderAdapter.Builder<SpringPackageScannerElement>().apply {
        addProvider("%c") {
            it.getClassReducedValue(packageLineFormatterVariableReducerMap["%c"]!!)
        }
        addProvider("%V") {
            it.getClassReducedValue(packageLineFormatterVariableReducerMap["%V"]!!)
        }
        addProvider("%P") { it.packageName }
    }.build()
}
