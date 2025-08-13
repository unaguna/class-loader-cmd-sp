package jp.unaguna.classloader.core

import java.net.URL

interface ScannedElement<E> {
    val element: E
    val depth: Int
    val className: String
    val classSource: URL?
}
