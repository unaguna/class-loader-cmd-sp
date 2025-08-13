package jp.unaguna.classloadersp.cmd

import jp.unaguna.classloadersp.SubclassDefinitionProvider
import jp.unaguna.classloadersp.tree.ExtendClassTree
import jp.unaguna.classloadersp.utils.classpathSpecToURLArray
import java.net.URLClassLoader

fun main() {
    val classpath = classpathSpecToURLArray("C:\\programs\\class-loader-cmd-sandbox\\one.jar;C:\\programs\\class-loader-cmd-sandbox\\two.jar")
    val classLoader = URLClassLoader(classpath, null)
    val defProvider = SubclassDefinitionProvider(
        classLoader,
        superClass = classLoader.loadClass("com.A"),
    )

    val tree = ExtendClassTree(classLoader)

    // 指定パッケージ配下をスキャン
    for (bd in defProvider) {
        tree.append(bd)
    }

    for ((bd, depth) in tree.iterator()) {
        println("\t".repeat(depth) + bd?.beanClassName + "\t" + bd?.source)
    }
}
