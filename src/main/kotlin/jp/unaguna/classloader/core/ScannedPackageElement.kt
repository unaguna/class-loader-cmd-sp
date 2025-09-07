package jp.unaguna.classloader.core

interface ScannedPackageElement<E> {
    val element: E
    val packageName: String
}
