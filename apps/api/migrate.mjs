import { execFileSync, spawnSync } from "node:child_process";

const databaseKeys = [
  "FOODMARKT_DB_URL",
  "FOODMARKT_DB_USERNAME",
  "FOODMARKT_DB_PASSWORD",
];
const apiDirectory = new URL(".", import.meta.url);

function compose(...args) {
  return execFileSync("docker", ["compose", ...args], {
    cwd: apiDirectory,
    encoding: "utf8",
    stdio: ["ignore", "pipe", "pipe"],
  });
}

function localDatabase() {
  let binding;
  try {
    binding = compose("port", "postgres", "5432");
  } catch {
    compose("up", "-d", "--no-recreate", "--wait", "--wait-timeout", "60", "postgres");
    binding = compose("port", "postgres", "5432");
  }
  const port = binding.trim().match(/:(\d+)$/)?.[1];
  if (!port) throw new Error("Docker Compose did not report a published PostgreSQL port.");

  const [database, username, password] = compose(
    "exec", "-T", "postgres", "printenv", "POSTGRES_DB", "POSTGRES_USER", "POSTGRES_PASSWORD",
  ).split(/\r?\n/);
  if (!database || !username || !password) {
    throw new Error("The Compose postgres service is missing database settings.");
  }

  return {
    FOODMARKT_DB_URL: `jdbc:postgresql://127.0.0.1:${port}/${encodeURIComponent(database)}`,
    FOODMARKT_DB_USERNAME: username,
    FOODMARKT_DB_PASSWORD: password,
  };
}

try {
  const missing = databaseKeys.filter((key) => !process.env[key]?.trim());
  let env = process.env;
  if (missing.length === databaseKeys.length) {
    env = { ...env, ...localDatabase() };
  } else if (missing.length > 0) {
    throw new Error(`Set ${missing.join(", ")} to complete the explicit database configuration.`);
  }

  const result = spawnSync(process.execPath, ["--run", "api:gradle", "--", process.argv[2]], {
    env,
    stdio: "inherit",
  });
  if (result.error) throw result.error;
  process.exitCode = result.status ?? 1;
} catch (error) {
  console.error(error.stderr?.trim() || error.message);
  process.exitCode = 1;
}
