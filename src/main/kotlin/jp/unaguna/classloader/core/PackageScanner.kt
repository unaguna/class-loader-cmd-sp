package jp.unaguna.classloader.core

interface PackageScanner<E, CE, S : ScannedPackageElement<E>, CS : ScannedClassElement<CE>> {
    fun scan(): Iterator<S>
    fun applyClassCounter(counter: ClassCounterFactory<CE, CS>)
}
