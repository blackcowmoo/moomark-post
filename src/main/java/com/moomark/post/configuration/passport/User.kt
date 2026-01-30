package com.moomark.post.configuration.passport

class User {
    var id: String? = null
    var email: String? = null
    var nickname: String? = null
    var picture: String? = null
    var authProvider: String? = null

    val userId: String
        get() = authProvider + "@" + id
}
