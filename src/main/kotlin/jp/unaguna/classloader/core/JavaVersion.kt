package jp.unaguna.classloader.core

data class JavaVersion(val major: Int, val minor: Int) : Comparable<JavaVersion> {
    override fun compareTo(other: JavaVersion): Int {
        return if (this.major != other.major) {
            this.major - other.major
        } else {
            this.minor - other.minor
        }
    }

    override fun toString(): String {
        return "$major.$minor"
    }
}
