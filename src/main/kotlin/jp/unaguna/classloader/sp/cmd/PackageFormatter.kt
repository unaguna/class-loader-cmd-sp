package jp.unaguna.classloader.sp.cmd

import jp.unaguna.classloader.sp.SpringClassReducerType
import jp.unaguna.classloader.sp.SpringPackageScannerElement
import jp.unaguna.fmtbuilder.DataFormat
import jp.unaguna.fmtbuilder.ValueProviderAdapter

fun createPackageLineFormatter(
    format: String?,
): DataFormat {
    return DataFormat.fromPrintfFormat(format ?: "%P")
}

val packageLineFormatterVariableReducerMap = mapOf<String, SpringClassReducerType<*>>(
    "%V" to SpringClassReducerType.MaxJavaVersion,
)

@Suppress("CyclomaticComplexMethod")
fun createPackageValueProviderAdapter(): ValueProviderAdapter<SpringPackageScannerElement> {
    return ValueProviderAdapter.Builder<SpringPackageScannerElement>().apply {
        addProvider("%V") {
            it.getClassReducedValue(packageLineFormatterVariableReducerMap["%V"]!!)
        }
        addProvider("%P") { it.packageName }
    }.build()
}
