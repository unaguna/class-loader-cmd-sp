package jp.unaguna.classloader.sp.cmd

import jp.unaguna.classloader.sp.SpringClasspathScanner
import jp.unaguna.classloader.utils.classpathSpecToURLArray
import java.net.URLClassLoader
import kotlin.collections.iterator

fun main() {
    val classpath = classpathSpecToURLArray("C:\\programs\\class-loader-cmd-sandbox\\one.jar;C:\\programs\\class-loader-cmd-sandbox\\two.jar")
    val classLoader = URLClassLoader(classpath, null)
    val scanner = SpringClasspathScanner(classLoader).apply {
        subclassOf(classLoader.loadClass("java.lang.Object"))
        asClassExtensionTree()
    }

    // 指定パッケージ配下をスキャン
    for (scanned in scanner.scan()) {
        println("\t".repeat(scanned.depth) + scanned.className + "\t" + scanned.classSource)
    }
}
