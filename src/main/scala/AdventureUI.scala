import scala.io.StdIn.readLine

object AdventureUI extends App {

  final val reverseMarker = Console.REVERSED
  final val resetMarker = Console.RESET
  final val redMarker = Console.RED
  this.run()

  private def run() = {
    println(Game.welcomeMessage)
    println(Game.currentScene.onStart())
    while (!Game.isOver) {
      this.playTurn()
    }
  }

  private def playTurn() = {
    println()
    val command = readLine(Console.GREEN + "Command: " + Console.RESET)
    val turnReport = Game.playTurn(command)
    if (!turnReport.isEmpty) {
      println(turnReport)
    }
  }

}
