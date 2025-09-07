package jp.unaguna.classloader.core

interface ClasspathScanner<E, S : ScannedClassElement<E>> {
    fun scan(): Iterator<S>
    fun subtypeOf(cls: Class<*>)
    fun annotatedBy(cls: Class<out Annotation>)
    fun pattern(classNamePatterns: Iterable<String>)
    fun inPackage(basePackage: String)
    fun asClassExtensionTree()
}

interface ClasspathScannerResettable<E, S : ScannedClassElement<E>> : ClasspathScanner<E, S> {
    fun clearConditions()
}
