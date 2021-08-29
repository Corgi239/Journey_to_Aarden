object Game {

  val welcomeMessage = "\nWelcome to the Journey to Aarden!\n\nThis game is about exploring various locations and talking with beings that inhabit them.\n" +
    "\nWhile exploring areas, you will always have access to a set of commands, that includes:\n" +
    "\t- Look around (reads the description of the location you are currently in)\n" +
    "\t- Go/go to/go to the <location name> (allows you to travel to different locations)\n" +
    "\t- Talk/talk to/talk with <npc name> (allows you to chat with various non-player characters)\n" +
    "\t- Help (gives you the list of npcs in the current location and a list of areas available for traveling from the current location\n" +
    "\t- Inventory (list items in your inventory)\n" +
    "\t- Examine <item name> (reads you the description of the item)\n" +
    "All available locations and npcs appear in the narration during the game, and are " + AdventureUI.reverseMarker + "highlighted" + AdventureUI.resetMarker + "\n\n" +
    "In dialogues, however, you are on your own! Write whatever feels right,\nand the game will do its best to interpret your answer\n" +
    "Now it's time for you to get into the game. Here goes...\n\n" +
    "At long last, your journey appears to be near it's resolution.\n" +
    "For decades you have been tormented by a powerful " + AdventureUI.redMarker + "demon" + AdventureUI.resetMarker + " that has seeded himself into your very mind,\nand is slowly taking over control of your thoughts and actions.\n" +
    "Several months ago you have come across a rumor, that a priest by the name of Fargoth, who lives in the city of Aarden,\nhas been able to help people with somewhat similar conditions.\n" +
    "Your travels have left you with nothing but a couple gold coins and your trusly staff.\n" +
    "You are now somewhere around Aarden, althoigh you are not quite sure how to get there... \nperhaps some of the locals might be able to give you directions.\n" +
    "---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"


  var currentScene: Scene = Riverside

  var isOver = false

  def playTurn(command: String): String = {
    currentScene.execute(command)
  }

  def moveToScene(destination: Scene): String = {
    var report = currentScene.onExit()
    currentScene = destination
    report += currentScene.onStart()
    report
  }

}
