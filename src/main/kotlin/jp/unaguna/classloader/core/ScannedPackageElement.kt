package jp.unaguna.classloader.core

interface ScannedPackageElement<E> {
    val element: E
    val packageName: String

    /**
     * Returns the number of classes that match the specified condition.
     *
     * @throws IllegalArgumentException If the count cannot be done under the specified condition
     */
    fun <R> getClassReducedValue(condition: ClassReducerType<R>): R
}
