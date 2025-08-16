package jp.unaguna.classloader.core

interface ClasspathScanner<E, S: ScannedElement<E>> {
    fun scan(): Iterator<S>
    fun subtypeOf(cls: Class<*>)
    fun pattern(classNamePatterns: Iterable<String>)
    fun inPackage(basePackage: String)
    fun asClassExtensionTree()
}

interface ClasspathScannerResettable<E, S: ScannedElement<E>> : ClasspathScanner<E, S> {
    fun clearConditions()
}
