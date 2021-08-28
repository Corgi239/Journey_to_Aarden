

abstract class DialogueSystem extends Scene{

  protected val owner: NPC
  protected var currentNode: DialogueNode
  protected val goodbyeMessage: String
  protected val unrecognizedCommandResponce: String

  protected val reverseMarker = AdventureUI.reverseMarker
  protected val resetMarker = AdventureUI.resetMarker
  protected val redMarker = AdventureUI.redMarker

  private final val continueCommand = "continue"
  private final val binaryCommand1 = "positiveResult"
  private final val binaryCommand2 = "negativeResult"

  protected val nodes: Map[String, DialogueNode]
  protected val branches: Map[(DialogueNode, String), DialogueNode]

  abstract class DialogueNode {
    def reply(): String
    def processCommand(command: String): String = command.toLowerCase
  }

  class SimpleDialogueNode(content: String) extends DialogueNode {
    def reply() = "\n" + content
  }

  class BinaryDialogueNode(content: String) extends DialogueNode {
    def reply() = "\n" + content

    def condition: Boolean = true

    override def processCommand(command: String): String = {
      if (condition) binaryCommand1 else binaryCommand2
    }
  }

  private def runNode(): String = {
   var res = currentNode.reply()
   if (branches.contains((currentNode, continueCommand))) {
     currentNode = branches((currentNode, continueCommand))
     res += runNode()
   }
   if (currentNode.isInstanceOf[BinaryDialogueNode]) {
      res += execute(currentNode.processCommand(""))
   }
   res
  }



  def execute(inputCommand: String): String = {
    val command = inputCommand
    if (command.toLowerCase == "exit") {
      exitDialogue()
    } else {
      val processedCommand = this.currentNode.processCommand(command)
      val nextNode = this.branches.get((this.currentNode, processedCommand))
      if (nextNode.isDefined) {
        this.currentNode = nextNode.get
        this.runNode()
      } else {
        "\n" + unrecognizedCommandResponce
      }
    }
  }

  def exitDialogue(): String = {
    Game.moveToScene(owner.getLocation)
  }

  def onExit(): String = "\n" + goodbyeMessage + "\n"

  override def getHelpInfo: String = ""

}