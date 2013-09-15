package yangbajing.log

trait Loggable {
  @transient val logger = Logger(this.getClass)
}

trait LoggableLazy {
  @transient lazy val logger = Logger(this.getClass)
}
