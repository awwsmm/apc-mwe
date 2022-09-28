// Main.scala
import akka.persistence.cassandra.testkit.CassandraLauncher
import com.datastax.oss.driver.api.core.CqlSession

import java.net.InetSocketAddress
import java.nio.file.Paths
import scala.annotation.tailrec
import scala.concurrent.duration.{Duration, DurationInt, DurationLong, FiniteDuration}
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

object Main extends App {
	import Helpers._

	// put your custom cassandra-bundle.jar in <project_root>/target/cassandra/
	private val cassandraDirectory = Paths.get("target", "cassandra")

	CassandraLauncher
		.start(
			cassandraDirectory = cassandraDirectory.toFile, // load the custom cassandra-bundle.jar
			configResource = CassandraLauncher.DefaultTestConfigResource,
			clean = false, // do not clean the directory where we've placed the cassandra-bundle.jar file
			port = 0, // use a random port
			classpath = Seq.empty
		)

	awaitAssert({
		testCassandra()
	}, 45.seconds)

	CassandraLauncher.stop()

	Try(testCassandra()) match {
		case Failure(_) => println("all good!")
		case Success(_) => throw new Exception("not all good :(")
	}

	// these methods taken from CassandraLauncherSpec in akka-persistence-cassandra
	object Helpers {
		def awaitAssert[A](a: => A, max: Duration = Duration.Undefined, interval: Duration = 100.millis): A = {
			def now: FiniteDuration = System.nanoTime.nanos
			val _max = 100.seconds // remainingOrDilated(max)
			val stop = now + _max

			@tailrec
			def poll(t: Duration): A = {
				// cannot use null-ness of result as signal it failed
				// because Java API and not wanting to return a value will be "return null"
				var failed = false
				val result: A =
					try {
						val aRes = a
						failed = false
						aRes
					} catch {
						case NonFatal(e) =>
							failed = true
							if ((now + t) >= stop) throw e
							else null.asInstanceOf[A]
					}

				if (!failed) result
				else {
					Thread.sleep(t.toMillis)
					poll((stop - now) min interval)
				}
			}

			poll(_max min interval)
		}

		def testCassandra(): Unit = {
			val session =
				CqlSession
					.builder()
					.withLocalDatacenter("datacenter1")
					.addContactPoint(new InetSocketAddress("localhost", CassandraLauncher.randomPort))
					.build()
			try session.execute("SELECT now() from system.local;").one()
			finally {
				session.close()
			}
		}
	}

}