package jp.unaguna.classloader.sp.cmd.args

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import jp.unaguna.classloader.cmd.validator.NonOption
import jp.unaguna.classloader.sp.SpringPackageScanner
import jp.unaguna.classloader.sp.cmd.createPackageLineFormatter
import jp.unaguna.classloader.sp.cmd.createPackageValueProviderAdapter
import jp.unaguna.classloader.sp.cmd.packageLineFormatterVariableReducerMap
import jp.unaguna.classloader.utils.classpathSpecToURLArray
import jp.unaguna.fmtbuilder.TableDataFormatIterator
import java.net.URLClassLoader

@Parameters(
    commandDescription = "list packages in the classpath"
)
class LsPackages : SubCommand {
    override val name = "ls-packages"

    @Parameter(description = "[PACKAGE]...", validateWith = [NonOption::class])
    var packages: List<String> = mutableListOf()

    @Parameter(names = ["-cp", "--classpath"], description = "the classpath to scan", order = 0)
    var classpath: String? = null

    @Parameter(names = ["--format"], description = "format in printf specification", category = "Format", order = 211)
    var format: String? = null

    override fun execute(commonArgs: CommonArgs) {
        val classpathStr = classpath ?: commonArgs.classpath
        val classpath = classpathStr?.let { classpathSpecToURLArray(it) }
        val classLoader = if (classpath != null) {
            URLClassLoader(classpath, null)
        } else {
            javaClass.classLoader
        }

        val lineFormatter = createPackageLineFormatter(format)

        val scannedAdapter = createPackageValueProviderAdapter()
        val scanner = SpringPackageScanner(classLoader).apply {
            if (packages.isNotEmpty()) {
                pattern(packages)
            }
            for (variableName in lineFormatter.variableNames) {
                val reducerType = packageLineFormatterVariableReducerMap[variableName]
                    ?: continue
                applyClassReducer(reducerType)
            }
        }
        val bufferedIterator = TableDataFormatIterator(
            lineFormatter,
            scanner.scan(),
            scannedAdapter,
        )

        // 指定パッケージ配下をスキャン
        for (formattedLine in bufferedIterator) {
            println(formattedLine)
        }
    }
}
