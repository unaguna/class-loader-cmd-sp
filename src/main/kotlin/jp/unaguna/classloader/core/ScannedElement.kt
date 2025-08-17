package jp.unaguna.classloader.core

import java.net.URL

interface ScannedElement<E> {
    val element: E
    val depth: Int
    val className: String
    val classSource: URL?
    val isAbstract: Boolean
    val isInterface: Boolean
    val isFinal: Boolean
    val isAnnotation: Boolean
    val visibility: Visibility
    val major: Int
    val minor: Int
    val serialVersionUID: Long?
}

enum class Visibility {
    PUBLIC,
    PACKAGE_PRIVATE,
    PROTECTED,
    PRIVATE,
}
