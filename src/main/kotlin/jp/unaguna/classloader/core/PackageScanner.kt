package jp.unaguna.classloader.core

interface PackageScanner<E, CE, S : ScannedPackageElement<E, CT>, CS : ScannedClassElement<CE>, CT> {
    fun scan(): Iterator<S>
    fun applyClassReducer(reducerFactory: ClassReducerFactory<CE, CS, CT, *>)
    fun pattern(packageNamePatterns: Iterable<String>)
}
