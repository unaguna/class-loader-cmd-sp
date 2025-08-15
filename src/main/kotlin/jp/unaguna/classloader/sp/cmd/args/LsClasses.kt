package jp.unaguna.classloader.sp.cmd.args

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import jp.unaguna.classloader.sp.SpringClasspathScanner
import jp.unaguna.classloader.sp.cmd.ClassFormatter
import jp.unaguna.classloader.utils.classpathSpecToURLArray
import java.net.URLClassLoader
import kotlin.collections.iterator

@Parameters(
    commandDescription = "list classes in the classpath"
)
class LsClasses: SubCommand {
    override val name = "ls-classes"

    @Parameter(names = ["-cp", "--classpath"], description = "The classpath to scan")
    var classpath: String? = null

    @Parameter(names = ["--inherit"], description = "list only classes which extends or implements specified class")
    var inherit: String? = null

    @Parameter(names = ["-l"], description = "use a long listing format")
    var longFormat: Boolean = false

    @Parameter(names = ["--ext-tree"], description = "output classes as an extension tree")
    var asExtendTree: Boolean = false

    override fun execute(commonArgs: CommonArgs) {
        val classpathStr = classpath ?: commonArgs.classpath
        val classpath = classpathStr?.let { classpathSpecToURLArray(it) }
        val classLoader = if (classpath != null) {
            URLClassLoader(classpath, null)
        } else {
            javaClass.classLoader
        }

        val scanner = SpringClasspathScanner(classLoader).apply {
            inherit?.let { inherit ->
                subtypeOf(classLoader.loadClass(inherit))
            }
            if (asExtendTree) {
                asClassExtensionTree()
            }
        }

        val lineFormatter = ClassFormatter(
            longFormat = longFormat,
        )

        // 指定パッケージ配下をスキャン
        for (scanned in scanner.scan()) {
            println(lineFormatter.format(scanned))
        }
    }
}
