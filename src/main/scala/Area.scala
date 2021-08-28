import scala.util.matching.Regex

abstract class Area(val name: String) extends Scene {

  protected val reverseMarker = AdventureUI.reverseMarker
  protected val resetMarker = AdventureUI.resetMarker
  protected val redMarker = AdventureUI.redMarker
  private var visitedFlag = false
  var neighbours: Map[String, Area] = Map()
  var items: Map[String, Item] = Map()
  var npcs: Map[String, NPC] = Map()
  protected val lookAroundReg = new Regex("""look around""")
  protected val goReg = new Regex("(go to the|go to|go) (.*)")
  protected val talkReg = new Regex("(talk to|talk with|talk) (.*)")
  protected val examineReg = new Regex("(examine|examine the) (.*)")

  override def getHelpInfo: String = "Go to [" + this.neighbours.keys.mkString(", ") + "]\nTalk to [" + npcs.keys.mkString(", ") + "]" +
    "\nLook around" + "\nInventory" + "\nExamine [" + Player.getItemsInBakcpack.mkString(", ") + "]"

  def description: String

  def shortDescription: String

  def neighbour(areaName: String): Option[Area] = this.neighbours.get(areaName)

  def addNeighbour(areaName: String, newNeighbour: Area) = {
    neighbours += (areaName -> newNeighbour)
  }

  def npc(npcName: String): Option[NPC] = this.npcs.get(npcName)

  def removeNpc(npcName: String) = {
    npcs -= npcName
  }

  override def onStart(): String = {
    if (!visitedFlag) {
      visitedFlag = true
      "\n" + description
    } else {
      "\n" + shortDescription
    }
  }

  override def onExit() = ""

  override def execute(userCommand: String): String = {
    val command = userCommand.toLowerCase()
     command match {
       case lookAroundReg()     => "\n" + description
       case goReg(garbage, destination)  => if (this.neighbours.contains(destination)) Game.moveToScene(neighbours(destination))
                                  else s"\nYou don't know how to get to $destination from here"
       case talkReg(garbage, target)     => if (this.npcs.contains(target)) Game.moveToScene(npcs(target).getDS)
                                  else s"\nThere is no $target here"
       case "help"             => "Commands available for this location:\n" + Game.currentScene.getHelpInfo
       case "inventory"         => "You rummage through your items. Here's what you find:\n" + Player.getInventory
       case examineReg(garbage, target) => if (Player.hasItem(target)) Player.getItemDescription(target) else s"You do not have $target"
       case _                   => "Command not recognized, please try again or enter " + reverseMarker + "help" + resetMarker + " for a list of available commands"
     }
  }
}

/**#################### PIER ######################
  *
  */

object Pier extends Area("pier") {

  this.neighbours = Map[String, Area]("riverside" -> Riverside)
  this.npcs = Map("fisherman" -> Fisherman)

  var descrString = "A wooden floating pier extends into the river. The wood creaks and sways under your feet as you step onto the planks.\n" +
                    "From here you have a view of the serene " + reverseMarker + "riverside" + resetMarker + ".\n" +
                    "In front of you the " + reverseMarker + "fisherman" + resetMarker + " is collecting his various fishing hooks, bait and lures into a small woven basket.\n" +
                    "He greets you with a nod and a smile."

  def timePasses(): Unit = {
     descrString = "A wooden floating pier extends into the river. The wood creaks and sways under your feet as you step onto the planks.\n" +
                   "From here you have a view of the serene " + reverseMarker + "riverside" + resetMarker + ".\n" +
                   "The fisherman seems to have left, the pier is now empty"
    removeNpc("fisherman")
  }

  override def description: String = descrString

  override def shortDescription: String = "You are standing on the pier."
}

/**#################### RIVERSIDE ######################
  *
  */

object Riverside extends Area("riverside") {

  this.neighbours = Map("pier" -> Pier)

  override def description: String = "You've made it to the bank of the Cobalt Creek. The water here is so clear that you can see schools of tiny fish swimming about.\n" +
                                     "A distant song catches you attention, and as you turn to look you notice a fisherman standing atop a wooden " + reverseMarker + "pier" + resetMarker + " situated further downstream.\n" +
                                     "The fisherman is humming a cheerful tune as he's packing up his gear and today's catch."

  override def shortDescription: String = "You are standing on the bank of the Cobalt Creek."
}

/**#################### GATES OF AARDEN ######################
  *
  */

object GatesOfAarden extends Area("gates of Aarden") {

  override def onStart(): String = {
    Pier.timePasses()
    super.onStart()
  }

  this.neighbours = Map("right" -> MysteriousPath,
                         "peir" -> Pier)

  this.npcs = Map("guard" -> Guard, "merchant" -> Fisherman, "woman" -> Woman)

  override def description: String = "You are standing at the gates of the great city of Aarden. \n" +
                                     "The path to the city is blocked by a set of massive wooden doors, reinforced with forged metal hinges and rivets.\n" +
                                     "On the walls above the gate you see a " + reverseMarker + "guard" + resetMarker + ", who seems to be either completely lost in thought or simply dozing off.\n" +
                                     "To the " + reverseMarker + "right" + resetMarker + " you see a narrow path that winds along the city walls. Sounds of a lute being played in the distance eminate from wherever this path leads towards.\n" +
                                     "To the left of the gate, you see a " + reverseMarker + "woman" + resetMarker + " dressed in foreign clothes.\nShe's picking up various trinkets and baubles from a bear hide spread out on the ground, and moving them into the saddblebags of her donkey.\n"

  override def shortDescription: String = "You stand at the gates of the great city of Aarden.\nHere you can spot a " + reverseMarker + "guard" + resetMarker + " at the top of the gates, a " + reverseMarker + "woman" + resetMarker + " packing away her wares and a path to the " + reverseMarker + "right" + resetMarker + "."

}

/**#################### MYSTERIOUS PATH ######################
  *
  */

object MysteriousPath extends Area("mysterious path") {

  var robbed = false

  this.neighbours = Map("aarden" -> GatesOfAarden)

  this.npcs = Map("elf" -> Elf)

  override def description: String = "The path veers off into the nearby woods, and eventually leads you to a small forest clearing, where you find a company of humans, elves and halflings, all partaking in various partying activities.\n" +
                                      "At the moment you enter the clearing, the whole merry group joins their voices in a high note of a drunken song, before collectivelly breaking out in laughter.\n" +
                                      "You notice an " + reverseMarker + "elf" + resetMarker + " among the group who seems just a little more sober than the others, perhaps even sober enough to have a conversation with.\n" +
                                      "You are still free to go back to " + reverseMarker + "Aarden" + resetMarker + "."

  override def shortDescription: String = "You find yourself at the center or a small but rather rowdy celebration."

  override def onExit() = {
    if (robbed) {
      robbed = false
      super.onExit() + "\nAs you leave the party and head back to the gates, you pat yourself down, and discover that some of your gold is missing."
    } else {
      super.onExit()
    }

  }

}

/**#################### TEMPLE OF PHASNA ######################
  *
  */

object TempleOfPhasna extends Area("TempleOfPhasna") {

  this.neighbours = Map("aarden" -> GatesOfAarden)

  this.npcs = Map("fargoth" -> Fargoth)

  override def description: String = "The temple of Phasna looks nothing like you imagined.\nAfter all the wonderous tales you heard about this place,\nyou would have expecting to see an enormous palace, adorned with marble statues, or perhaps a towering cathedral with spires piercing the sky.\n" +
                                     "What you found instead is a house like any other in Aarden, the only dissernable features of this temple being a small plaque with cresent moon symbol on the door\nand faint smell of incense floating in the air." +
                                     "You take a deep breath, recounting decades worth of journey - all leading up this fateful moment, and give the old door a push. It open with a creak.\nAs you cast a glance inside the temple, you see a strange man sitting almost completely motionless behind a round wooden table.\n" +
                                     "This must be " + reverseMarker + "Fargoth" + resetMarker + ", the priest of Phasna that you have been searching for.\nHe's dressed in elegant, if maybe a little old-fashined, clothes. A deep-blue headband covers his hair and goes down over his eyes.\nYou notice the same cresent moon symbol painted in silver on the headband.\n"

  override def shortDescription: String = "You are in the temple of Phasna"


}