package jp.unaguna.classloader.core

interface PackageScanner<E, CE, S : ScannedPackageElement<E>, CS : ScannedClassElement<CE>> {
    fun scan(): Iterator<S>
    fun applyClassReducer(reducerFactory: ClassReducerFactory<CE, CS, *>)
    fun pattern(packageNamePatterns: Iterable<String>)
}
