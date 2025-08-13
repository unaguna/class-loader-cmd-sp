package jp.unaguna.classloader.utils

import java.io.File
import java.net.URL


fun classpathSpecToURLArray(spec: String): Array<URL> {
    return spec.split(File.pathSeparator)
        .map { File(it).toURI().toURL() }
        .toTypedArray()
}
