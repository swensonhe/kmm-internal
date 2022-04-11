object Ktor {
    private const val ktorVersion = "2.0.0"
    private const val logBackVersion = "1.2.10"
    private const val kotlinXSerializationVersion = "1.3.2"
    const val core = "io.ktor:ktor-client-core:${ktorVersion}"
    const val clientSerialization = "io.ktor:ktor-client-serialization:${ktorVersion}"
    const val kotlinXSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinXSerializationVersion}"
    const val logging = "io.ktor:ktor-client-logging:${ktorVersion}"
    const val logback = "ch.qos.logback:logback-classic:${logBackVersion}"
    const val android = "io.ktor:ktor-client-android:${ktorVersion}"
    const val ios = "io.ktor:ktor-client-ios:${ktorVersion}"
}