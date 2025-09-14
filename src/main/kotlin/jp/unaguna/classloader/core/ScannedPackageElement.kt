package jp.unaguna.classloader.core

interface ScannedPackageElement<E, CT> {
    val element: E
    val packageName: String

    /**
     * Returns the number of classes that match the specified condition.
     *
     * @throws IllegalArgumentException If the count cannot be done under the specified condition
     */
    fun getClassCount(condition: CT): Int
}
