package com.foodmarkt.foodmarkt

import org.flywaydb.core.Flyway

fun main(args: Array<String>) {
    val flyway = Flyway.configure()
        .dataSource(
            requireEnvironment("FOODMARKT_DB_URL"),
            requireEnvironment("FOODMARKT_DB_USERNAME"),
            requireEnvironment("FOODMARKT_DB_PASSWORD"),
        )
        .load()

    when (args.single()) {
        "migrate" -> println("Applied ${flyway.migrate().migrationsExecuted} migration(s).")
        "info" -> flyway.info().all().forEach {
            println("${it.version ?: "-"}  ${it.state}  ${it.description}")
        }
        "validate" -> {
            flyway.validate()
            println("Migration validation passed.")
        }
        else -> error("Expected migrate, info, or validate.")
    }
}

private fun requireEnvironment(name: String): String =
    System.getenv(name)?.takeIf { it.isNotBlank() }
        ?: error("Set $name in the environment or the workspace .env file.")
