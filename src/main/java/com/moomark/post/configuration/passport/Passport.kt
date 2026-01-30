package com.moomark.post.configuration.passport

import java.sql.Timestamp

data class Passport(
    var exp: Timestamp? = null, // expired timestamp
    var key: String? = null,
    var hash: String? = null
)
