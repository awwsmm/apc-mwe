# apc-mwe

To create your own Mac M1 / ARM64 / AArch64 akka-persistence-cassandra `cassandra-bundle.jar` file

1. `git clone git@github.com:akka/akka-persistence-cassandra.git`
2. Make sure you're using Java 8 (tested with `8.0.345-librca` and [Azul Zulu 8](https://www.azul.com/downloads/?version=java-8-lts&os=macos&architecture=arm-64-bit&package=jdk))
3. Open `akka-persistence-cassandra` in IntelliJ and check the Java version under File > Project Structure > Project Settings > Project. Make sure the SDK is set to the Java 8 version from step 2.
4. Add `dependencyOverrides += "net.java.dev.jna" % "jna" % "5.12.1"` to the `cassandraBundle` project in the `build.sbt` of `akka-persistence-cassandra`
5. `sbt cassandraBundle/assembly` to create `cassandra-bundle/target/bundle/akka.persistence.cassandra.launcher/cassandra-bundle.jar` (you might have to comment out a few broken test suites to get this to run)
6. Check your work by running the `CassandraLauncherSpec` in `akka-persistence-cassandra`
7. Now you can use your `cassandra-bundle.jar` to run the `CassandraLauncher` in third-party projects, like this one!