package com.moomark.post.exception

class JpaException : Exception {
    val code: Int

    constructor(msg: String?, code: Int) : super(msg) {
        this.code = code
    }

    constructor(msg: String?) : super(msg) {
        this.code = DEFAULT_ERROR_CODE
    }

    companion object {
        private const val serialVersionUID = 6786491966940496018L
        private const val DEFAULT_ERROR_CODE = 100000
    }
}
