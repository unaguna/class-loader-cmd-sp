package jp.unaguna.classloader.sp.metaloader

import jp.unaguna.classloader.core.Visibility

data class StaticClassData(
    val visibility: Visibility,
    val major: Int,
    val minor: Int,
    val serialVersionUID: Long?,
)
