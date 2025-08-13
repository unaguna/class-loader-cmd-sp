package jp.unaguna.classloader.core

import java.net.URL

interface ScannedElement<E> {
    val element: E
    val depth: Int
    val className: String
    val classSource: URL?
    val isAbstract: Boolean
    // TODO: その他のメタデータ (参考: https://spring.pleiades.io/spring-framework/docs/current/javadoc-api/org/springframework/core/type/AnnotationMetadata.html)
}
