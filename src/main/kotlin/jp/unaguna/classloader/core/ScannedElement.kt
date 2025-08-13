package jp.unaguna.classloader.core

interface ScannedElement<E> {
    val element: E
    val depth: Int
}
