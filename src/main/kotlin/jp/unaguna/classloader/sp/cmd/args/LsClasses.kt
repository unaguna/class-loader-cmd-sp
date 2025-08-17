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
class LsClasses : SubCommand {
    override val name = "ls-classes"

    @Parameter(description = "[CLASS]...")
    var classes: List<String> = mutableListOf()

    @Parameter(names = ["-cp", "--classpath"], description = "the classpath to scan", order = 0)
    var classpath: String? = null

    @Parameter(
        names = ["--inherit"],
        description = "list only classes which extends or implements specified class",
        category = "Condition",
        order = 100,
    )
    var inherit: String? = null

    @Parameter(
        names = ["--annotated-by"],
        description = "list only classes annotated by specified annotation",
        category = "Condition",
        order = 100,
    )
    var annotatedBy: String? = null

    @Parameter(
        names = ["-l"],
        description = "use a long listing format",
        category = "Format",
        order = 200,
    )
    var longFormat: Boolean = false

    @Parameter(names = ["-ll"], description = "use a long-long listing format", category = "Format", order = 200)
    var longLongFormat: Boolean = false

    @Parameter(
        names = ["--ext-tree"],
        description = "output classes as an extension tree",
        category = "Format",
        order = 220,
    )
    var asExtendTree: Boolean = false

    @Parameter(names = ["--source"], description = "output source path of class", category = "Format", order = 210)
    var showSource: Boolean = false

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
            annotatedBy?.let { annotatedBy ->
                val cls = classLoader.loadClass(annotatedBy) as Class<out Annotation>
                annotatedBy(cls)
            }
            if (classes.isNotEmpty()) {
                pattern(classes)
            }
            if (asExtendTree) {
                asClassExtensionTree()
            }
        }

        val lineFormatter = ClassFormatter(
            longFormat = longFormat || longLongFormat,
            longStatus = longLongFormat,
            showSource = showSource,
        )

        // 指定パッケージ配下をスキャン
        for (scanned in scanner.scan()) {
            println(lineFormatter.format(scanned))
        }
    }
}
