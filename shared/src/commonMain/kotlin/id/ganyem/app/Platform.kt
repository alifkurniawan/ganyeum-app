package id.ganyem.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform