package jp.unaguna.classloader.core

interface ClasspathScanner<E, S: ScannedElement<E>> {
    fun scan(): Iterator<S>
    fun subclassOf(cls: Class<*>)
    fun inPackage(basePackage: String)
    fun asClassExtensionTree()
}

interface ClasspathScannerResettable<E, S: ScannedElement<E>> : ClasspathScanner<E, S> {
    fun clearConditions()
}
