package jp.unaguna.classloader.sp.cmd.args

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import jp.unaguna.classloader.cmd.validator.NonOption
import jp.unaguna.classloader.sp.SpringPackageScanner
import jp.unaguna.classloader.utils.classpathSpecToURLArray
import java.net.URLClassLoader
import kotlin.collections.iterator

@Parameters(
    commandDescription = "list packages in the classpath"
)
class LsPackages : SubCommand {
    override val name = "ls-packages"

    @Parameter(description = "[PACKAGE]...", validateWith = [NonOption::class])
    var packages: List<String> = mutableListOf()

    @Parameter(names = ["-cp", "--classpath"], description = "the classpath to scan", order = 0)
    var classpath: String? = null

    override fun execute(commonArgs: CommonArgs) {
        val classpathStr = classpath ?: commonArgs.classpath
        val classpath = classpathStr?.let { classpathSpecToURLArray(it) }
        val classLoader = if (classpath != null) {
            URLClassLoader(classpath, null)
        } else {
            javaClass.classLoader
        }

        val scanner = SpringPackageScanner(classLoader).apply {
            if (packages.isNotEmpty()) {
                pattern(packages)
            }
        }

        // 指定パッケージ配下をスキャン
        for (scanned in scanner.scan()) {
            println(scanned.packageName)
        }
    }
}
