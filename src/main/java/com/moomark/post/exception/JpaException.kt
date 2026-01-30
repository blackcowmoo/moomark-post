package com.moomark.post.exception

class JpaException : Exception {
    val code: Int

    constructor(msg: String?, code: Int) : super(msg) {
        this.code = code
    }

    constructor(msg: String?) : super(msg) {
        this.code = 100000
    }

    companion object {
        private const val serialVersionUID = 6786491966940496018L
    }
}
