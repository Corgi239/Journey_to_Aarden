

abstract class NPC(name: String) {

  protected val location: Scene

  protected val ds: DialogueSystem

  def getDS: Scene = ds

  def getLocation = location

}


/**#################### FISHERMAN ######################
  *
  */

object FishermanDS extends DialogueSystem {

  val owner = Fisherman
  override val goodbyeMessage = " -- Have a blessed day, mister!"
  override val unrecognizedCommandResponce: String = "Uhh, sorry, mister, I'm a simple fisherman, I wouldn't know anything about that.."

  override val nodes: Map[String, DialogueNode] = Map(
    "greetings"  ->     new SimpleDialogueNode(" -- Evenin', mister! What a marvelous day this has been, eh? A little while ago I caught a huge...\n " +
                                           "The man stops himself mid-sentene, then smiles softly, his already wrinkled face seemignly growing even more wrinkles\n" +
                                           " -- But you ain't here to listen to an old man babbling about his fishes, are ye? So, what can I for ye, mister?\n\n" +
                                           "> What would you like to ask the fisherman about? (type EXIT to end the dialogue)") {
      override def processCommand(command: String): String = {
        val fishKeywords = List("fish", "catch")
        val aboutFish = fishKeywords.exists( command.toLowerCase.contains(_) )
        if (aboutFish) return "fish"
        val directionsKeywords = List("aarden", "directions", "city")
        val aboutDirections = directionsKeywords.exists( command.toLowerCase.contains(_) )
        if (aboutDirections) return "aarden"
        "unknown"
      }
    },
    "fish story" ->     new SimpleDialogueNode(" -- Wait, ye do be wantin' to hear about the fish?.. Well alright then\n\n" +
                                           "The man tells you a few fishing stories, concluding them with:\n\n" +
                                           " -- Everything's good in moderation. You can't be letting go of every fish you catch, cause then you will be hungry.\nBut you also can't keep every fish you catch, cause then fishing won't be exiting anymore."
                                           ),
    "directions" ->     new SimpleDialogueNode(" -- Ooh, so ye be travelin' to Aarden, eh? Aye, it's not far..\n" +
                                           "The old man explains to you how to get to your destination. You can now go to " + reverseMarker + "Aarden" + resetMarker + "."
                                           ) {
                                              override def reply(): String = {
                                                Pier.addNeighbour("aarden", GatesOfAarden)
                                                super.reply()
                                              }
                                           },
    "anything else?" -> new SimpleDialogueNode(" -- Soo.. anything else ye need to ask me about, mister?\n\n" +
                                               "> What would you like to ask the fisherman about? (type EXIT to end the dialogue)") {
       override def processCommand(command: String): String = {
          val fishKeywords = List("fish", "catch")
          val aboutFish = fishKeywords.exists( command.toLowerCase.contains(_) )
          if (aboutFish) return "fish"
          val directionsKeywords = List("aarden", "directions", "city")
          val aboutDirections = directionsKeywords.exists( command.toLowerCase.contains(_) )
          if (aboutDirections) return "aarden"
          "unknown"
      }
    }


  )

  private val branchesShorthand: Map[(String, String), String] = Map(
    (("greetings", "fish")            -> "fish story"),
    (("greetings", "aarden")           -> "directions"),
    (("fish story", "continue")       -> "anything else?"),
    (("directions", "continue")       -> "anything else?"),
    (("anything else?", "aarden") -> "directions"),
    (("anything else?", "fish")       -> "fish story")
  )

  override val branches: Map[(DialogueNode, String), DialogueNode] = branchesShorthand.map( pair => (nodes(pair._1._1), pair._1._2) -> nodes(pair._2))

  override var currentNode = nodes("greetings")

  override def onStart(): String = currentNode.reply()

}

object Fisherman extends NPC("fisherman") {

  val location = Pier
  val ds = FishermanDS

}

/**#################### GUARD ######################
  *
  */

object GuardDS extends DialogueSystem {

  val owner = Guard
  override val goodbyeMessage: String = "Glory to Aarden!"
  override val unrecognizedCommandResponce: String = "Eh? Whacha say?"
  private var playerName = "Good Sir"
  def setPlayerName(name: String) = {
    this.playerName = name
  }
  override val nodes: Map[String, DialogueNode] = Map(
    "start" -> new SimpleDialogueNode(" --zzz\n\nThe guard appears to be vast asleep.\n") {
      override def processCommand(command: String) = {
        if (command.contains("!") || command.count(_.isUpper) > command.count(_.isLower)) {
          "!"
        } else {
          "."
        }
      }
    },
    "stay asleep" -> new SimpleDialogueNode("The guard appears to be vast asleep. Perhaps if you speak louder he might wake up..."){
      override def processCommand(command: String) = {
        if (command.contains("!") || command.count( _.isUpper ) > command.count( _.isLower )) {
          "!"
        } else {
          "."
        }
      }
    },
    "wake up" -> new SimpleDialogueNode(" --..Huh?.. Oh!.. Ahem.. Halt! Who goes there? State your name!") {
      override def processCommand(command: String): String = {
        GuardDS.setPlayerName(command.toLowerCase.trim.replaceAll("(i'm)|(my name is)|(i am)|(name is)", "").trim.split(" ").map( _.capitalize ).mkString(" "))
        "name given"
      }
    },
    "greetings" -> new SimpleDialogueNode(" -- Hail, oh Good Sir! I understand you seek passage into the great city of Aarden.") {
      override def reply(): String = {
         super.reply().replace("Good Sir", playerName)
      }
    },
    "enough gold?" -> new BinaryDialogueNode(" -- The royal tax for entering our glorious city is 20 gold pieces. Are you prepared to pay?\n\n" +
                                             s"You reach into your pocket and count your coins. Looks like you have <PlayerGold> gold pieces - ") {
      override def reply(): String = super.reply().replaceAll("<PlayerGold>", Player.playerGold.toString)
      override def condition: Boolean = Player.playerGold >= 20
    },
    "not enough" -> new BinaryDialogueNode("\ba few coins short of the needed 20. You look up at the guard and inform him that you cannot pay. The guard frowns and responds:\n\n" +
                                           " -- Regretfully, I can't let you in if you don't pay the tax. Such is the law of Aarden.\nIf you want to enter the city, you will have to find a way to get some coin.\n") {
      override def condition: Boolean = Player.hasItem("staff")
    },
    "staff offer" -> new SimpleDialogueNode("The woman you saw earlier calls out to you in a friendly voice:\n\n -- Hail, traveler! Couldn't help to overhear you're having some money issues.\nCome talk to me if you'd like to sell something of yours, I've got some coin to spare.\n\n" +
                                            "The guard points with his gaze towards the woman and says:\n\n -- See, in these lands there's hardly a place without opportunities."),
    "enough" -> new SimpleDialogueNode("\benough to pay for the entrance. You push the money through a small window in the gates.\n" +
                                       "A hand takes your offering, you hear the sound of coins clanking, and after a few secodns, you hear the guard say:\n\n" +
                                       " -- Alright, everything seems to be in order, come on through. Welcome to Aarden!\n\n" +
                                       "The gates open with a loud grinding noise. You are now free to make your way to the " + reverseMarker + "temple" + resetMarker + " in the city") {
      override def reply() = {
        Player.takePlayerGold(20)
        GatesOfAarden.addNeighbour("temple", TempleOfPhasna)
        super.reply()
      }
    },
    "anything else?" -> new SimpleDialogueNode(" -- Anything else I can help you with? Otherwise I'm going to go back to slee.. I mean my to duties.\n\n" + "> Ask the guard a question or type " +  reverseMarker + "exit" + resetMarker + " to leave the dialogue") {
      override def processCommand(command: String): String = {
          val questionKeywords = List("tradition", "custom")
          val aboutQuestion = questionKeywords.exists( command.toLowerCase.contains(_) )
          if (aboutQuestion) return "question"
          val taxKeywords = List("tax", "payment", "gold", "coin", "money")
          val aboutTax = taxKeywords.exists( command.toLowerCase.contains(_) )
          if (aboutTax) return "tax"
          "unknown"
      }
    },
    "swallow" -> new SimpleDialogueNode(" -- Oh, so you have heard of our little tradition then.. Such an odd practice, if you ask me. And yet, I must oblige.\n" +
                                        s"If you, oh Good Sir, manage to beat me in a contest of wits, I shall let you in without payment.\n" +
                                        "Now I must give you a riddle, as intricate as I can think of.. hmm.. Oh yeah, I've got one. Here then is my question:\n" +
                                        "What is the airspeed velocity of an unlaiden swallow?" ) {
      override def reply(): String = {
         super.reply().replace("Good Sir", playerName)
      }

      override def processCommand(command: String): String = {
        if (command.toLowerCase.contains("african")) {
          "correct"
        } else {
          "incorrect"
        }
      }
    },
    "be reasonable" -> new SimpleDialogueNode(" -- Hey, pal, I am just trying to do my job, alright? Please be reasonable with your questions."),
    "correct answer" -> new SimpleDialogueNode(" -- Uh?.. I.. I don't know that.. Well you are a sharp cookie, aren't you?\nI suppose that means I have to let you in then.\n\nThe gates open with a loud grinding noise. You are now free to make your way to the " + reverseMarker + "temple" + resetMarker + " in the city") {
      override def reply(): String = {
        GatesOfAarden.addNeighbour("temple", TempleOfPhasna)
        super.reply()
      }
    },
    "incorrect answer" -> new SimpleDialogueNode(" -- I am afraid that is not right. Don't beat yourself up about it though, I only know one person who was able to answer my riddle.")
  )

  private val branchesShorthand: Map[(String, String), String] = Map(
    (("start", ".")            -> "stay asleep"),
    (("start", "!")            -> "wake up"),
    (("stay asleep", "!")      -> "wake up"),
    (("stay asleep", ".")      -> "stay asleep"),
    (("wake up", "name given") -> "greetings"),
    (("greetings", "continue") -> "enough gold?"),
    (("enough gold?", "positiveResult") -> "enough"),
    (("enough gold?", "negativeResult") -> "not enough"),
    (("enough", "continue") -> "anything else?"),
    (("not enough", "negativeResult") -> "anything else?"),
    (("not enough", "positiveResult") -> "staff offer"),
    (("staff offer", "continue") -> "anything else?"),
    (("anything else?", "question") -> "swallow"),
    (("anything else?", "tax") -> "enough gold?"),
    (("anything else?", "unknown") -> "be reasonable"),
    (("be reasonable", "continue") -> "anything else?"),
    (("swallow", "correct") -> "correct answer"),
    (("swallow", "incorrect") -> "incorrect answer"),
    (("correct answer", "continue") -> "anything else?"),
    (("incorrect answer", "continue") -> "anything else?")
  )

  override val branches: Map[(DialogueNode, String), DialogueNode] = branchesShorthand.map( pair => (nodes(pair._1._1), pair._1._2) -> nodes(pair._2))

  override var currentNode = nodes("start")

  override def onStart(): String = currentNode.reply()
}

object Guard extends NPC("Aarden gate guard") {

  override val location = GatesOfAarden

  override val ds = GuardDS
}


/** #################### WOMAN ######################
  *
  */

object WomanDS extends DialogueSystem {

  val owner = Woman
  val goodbyeMessage: String = " -- Best of luck, traveler!"
  val unrecognizedCommandResponce: String = " -- I'm a smiple trader, I'm just here to exchange goods for coin.\n"

  override val nodes: Map[String, DialogueNode] = Map(
    "greetings" -> new SimpleDialogueNode(" -- Would you be interested in selling your staff?\nIt looks exquisite, I'll give you 50 gold for it, sound good?") {
      override def processCommand(command: String): String = {
        val positiveKeywords = List("yes", "sure", "why not", "agree")
        val positiveAnswer = positiveKeywords.exists( command.toLowerCase.contains(_) )
        if (positiveAnswer) return "yes"
        val negativeKeywords = List("no", "no.", "no, ", "decline", "refuse", "next time")
        val negativeAnswer = negativeKeywords.exists( command.toLowerCase.contains(_) )
        if (negativeAnswer) return "no"
        "uncertain"
      }
    },
    "no haggling" -> new SimpleDialogueNode(" -- Is that your way of trying to get a higher price out of me? I'm not into haggling, 50 gold or no deal"),
    "sold" -> new SimpleDialogueNode(" -- Great, here's your coin!") {
      override def reply(): String = {
        if (Player.hasItem("staff")) {
          Player.givePlayerGold(50)
          Player.removeItem("staff")
          super.reply()
        } else {
          "\n -- Hey, wait, you don't have any more staves. No coin for you!"
        }
      }
    },
    "suit yourself" -> new SimpleDialogueNode(" -- Well, suit yourself then. I'll be here if you change your mind!"),
    "anything else?" -> new SimpleDialogueNode(" -- Anything else you need from me?") {
      override def processCommand(command: String): String = {
        if (command.toLowerCase.contains("fargoth")) {
          "fargoth"
        } else {
          if (command.toLowerCase.contains("staff")) {
            "staff"
          } else {
            "unknown"
          }
        }

      }
    },
    "fargoth" -> new SimpleDialogueNode(" -- Oh yeah, I've heard of the guy.\nHe helpend my sister once, when she had the heebie-jeebies in her head. Cured her real quick he did!\nGood lad!" )
  )

  val branchesShorthand = Map(
    (("greetings", "yes") -> "sold"),
    (("greetings", "no") -> "suit yourself"),
    (("greetings", "uncertain") -> "no haggling"),
    (("no haggling", "continue") -> "greetings"),
    (("suit yourself", "continue") -> "anything else?"),
    (("sold", "continue") -> "anything else?"),
    (("anything else?", "fargoth") -> "fargoth"),
    (("anything else?", "staff") -> "greetings"),
    (("fargoth", "continue") -> "anything else?"),
  )

  override val branches: Map[(DialogueNode, String), DialogueNode] = branchesShorthand.map( pair => (nodes(pair._1._1), pair._1._2) -> nodes(pair._2))

  override var currentNode = nodes("greetings")

  override def onStart() = currentNode.reply()


}

object Woman extends NPC("elf") {

 val location = GatesOfAarden

 val ds = WomanDS

}


/** #################### ELF ######################
  *
  */

object ElfDS extends DialogueSystem {

  val owner = Elf
  val goodbyeMessage: String = " -- Safe travels, friend!"
  val unrecognizedCommandResponce: String = " -- Sorry, friend, you're gonna have to be a bit more specific on that responce.\n"

  override val nodes: Map[String, DialogueNode] = Map(
    "greetings" -> new SimpleDialogueNode(" -- Oi, over there! Hail, friend.\n What brings you to our little celebration?") {
      override def processCommand(command: String): String = {
        val aardenKeywords = List("aarden", "entrance", "passage", "guard", "tax", "payment", "fee", "gates")
        val aboutAarden = aardenKeywords.exists( command.toLowerCase.contains(_) )
        if (aboutAarden) return "aarden"
        val aboutFargoth = command.toLowerCase.contains("fargoth")
        if (aboutFargoth) return "fargoth"
        "unknown"
      }
    },
    "uncertain" -> new SimpleDialogueNode("Sorry, friend, I wouldn't know anything about that."),
    "fargoth" -> new SimpleDialogueNode(" -- Hm, yeah, I think I have heard of the guy - he lives in the temple of Phasna. I hear he's really weird.."),
    "aarden" -> new SimpleDialogueNode(" -- Looking to get into Aarden, huh?\nThose greedy bastards at the gates have started charging this ridiculus \"royal tax\" all of a sudden...\n\n" +
                                       "The elf looks at you with a sly smile.\n\n -- Friend, did you know there is another way to get the guard to let you in?\n" +
                                       "There is an old Aarden tradition, that states that whoever manages to outsmart the guard at the gates, gets to enter the city for free!\n" +
                                       "Just ask the guard about traditions, he will know what you mean.\n" +
                                       "Aaaand, if you're looking to gain an advantage in your upcoming competition with the guard, today's your lucky day!\n" +
                                       "Kormak here used to serve in the same patrol unit as the guy who's posted at the gates today.\n\n" +
                                       "The elf points towards a human with curly black hair, who's currently singing a very vulgar song terribly out of tune, while loosing a battle to his hiccups.\n\n" +
                                       " -- If you stay for a bit and drink with us, I am sure Kormak will tell you something that might aid you.\n" +
                                       "What do you say, wanna party with us for a while?") {
      override def processCommand(command: String): String = {
        val positiveKeywords = List("yes", "sure", "why not", "agree", "alright")
        val positiveAnswer = positiveKeywords.exists( command.toLowerCase.contains(_) )
        if (positiveAnswer) return "yes"
        val negativeKeywords = List("no", "no.", "no, ", "decline", "refuse", "next time")
        val negativeAnswer = negativeKeywords.exists( command.toLowerCase.contains(_) )
        if (negativeAnswer) return "no"
        "uncertain"
      }
    },
    "no drink" -> new SimpleDialogueNode(" -- Your loss, friend."),
    "kormak" -> new SimpleDialogueNode( " -- That's the spirit! Come, I'll poor you some ale.\n" +
                                        "Hey. Kormak, why don't you tell us about the swallow man one more time!\n\n" +
                                        "Kormak repsond in a sluggish voice\n\n" +
                                        " -- Wha?.. Oh, yeah yeah yeah, the swallow *hiccup* man! Ooooh, that story is holarious, I tell ya.\n" +
                                        "So, there is this dude who I used to scrub the shitters with back when I was *hiccup* in the royal guards.\n" +
                                        "He's crazy about swallows. You know, *hiccup* the little birdies?.. Yeah, he used to read every book about them that he could find!\n" +
                                        "I bet if he is posted at the *hiccup* gates, and has to give someone a riddle, he would totally ask some stupid crap about swallows!\n" +
                                        "Man, that guy is *huccup* nuts...\n" +
                                        "Oh, oh, and here's the kicker - yesterday I met this guy with  nice hair and *hiccup* glasses, reeeal smart guy, and he told me, would you believe it,\n" +
                                        "there's been a new *hiccup* discovery! Turns out there are two types of swallows - \"african\" and \"european\"!\n" +
                                        "Oh I bet if that guard dude gives someone a riddle about swallows \nand they respond with \"oh, do you mean african or european swallows?\", he's gonna loose his mind, I tell ya!\n\n" +
                                        "The elf laughs loudly. \n\n" +
                                        " -- See, I told you Kormak would say something valuable!") {
      override def reply(): String = {
        Player.takePlayerGold(10)
        MysteriousPath.robbed = true
        super.reply()
      }
    },

    "anything else?" -> new SimpleDialogueNode(" -- So, is there anything else you wanted to ask me about, friend?") {
      override def processCommand(command: String): String = {
        val aardenKeywords = List("aarden", "city", "entrance", "passage", "guard", "tax", "payment", "fee", "gates", "drink")
        val aboutAarden = aardenKeywords.exists( command.toLowerCase.contains(_) )
        if (aboutAarden) return "aarden"
        val aboutFargoth = command.toLowerCase.contains("fargoth")
        if (aboutFargoth) return "fargoth"
       "unknown"
      }
    }
  )

  val branchesShorthand = Map(
    (("greetings", "aarden") -> "aarden"),
    (("greetings", "fargoth") -> "fargoth"),
    (("greetings", "uncertain") -> "uncertain"),
    (("aarden", "yes") -> "kormak"),
    (("aarden", "no") -> "no drink"),
    (("no drink", "continue") -> "anything else?"),
    (("fargoth", "continue") -> "anything else?"),
    (("kormak", "continue") -> "anything else?"),
    (("anything else?", "aarden") -> "aarden"),
    (("anything else?", "fargoth") -> "fargoth")
  )

  override val branches: Map[(DialogueNode, String), DialogueNode] = branchesShorthand.map( pair => (nodes(pair._1._1), pair._1._2) -> nodes(pair._2))

  override var currentNode = nodes("greetings")

  override def onStart() = currentNode.reply()


}

object Elf extends NPC("elf") {

 val location = MysteriousPath

 val ds = ElfDS

}



/** #################### FARGOTH ######################
  *
  */

object FargothDS extends DialogueSystem {

  val owner = Fargoth
  override val goodbyeMessage: String = " -- Talk to me again when you wish to continue\n"
  override val unrecognizedCommandResponce: String = " -- Your words lack resolve. Take your time, make your desision, and then communicate your intent with a simple, staight-forward answer.\n"

  private def getBinaryAnswer(command: String): String = {
    val positiveKeywords = List("yes", "i am ready", "i'm ready", "i'm prepared", "i am prepared")
    val negativeKeywords = List("no ", "no.", "no, ", "i am not ready", "i'm not ready")
    if (negativeKeywords.exists( command.toLowerCase.contains(_) ) || command.toLowerCase == "no") {
      "no"
    } else {
      if (positiveKeywords.exists( command.toLowerCase.contains(_) )) {
        "yes"
      } else {
        "unknown"
      }
    }
  }

  override val nodes: Map[String, DialogueNode] = Map(
    "start" -> new SimpleDialogueNode("You open your mouth, thinking of words to explain your visit to the temple,\nbut before you have a chance to speak, the man says slowly, in a gentle, slightly cracking voice:\n\n" +
                                     " -- I know why you are here. I have been waiting for your arrival. I can sense your burden, oh Tormented One. Please, take a seat.\n\n" +
                                     "Fargoth gestures towards a chair, that is situated next to the table, opposite to the priest. You move towards the chair and sit down.\n" +
                                     "The scent of incense is stronger here, and as you breathe it in, you feel your mind becoming sharper, your thoughts focusing on your goal.\n\n" +
                                     " -- Give me your hand.\n\nYou stretch your right hand out to Fargoth, he puts his palms around it. A few seconds pass.\n\n" +
                                     " -- Yes, I can see " + redMarker + "him" + resetMarker + " clearly now.\nFreeing your mind from " + redMarker + "his" + resetMarker + " grip will not be easy, nor will it be void of risk.\nI can promise that I will do everything in my power to aid you, but the choice has to be yours.\n" +
                                     "You must decide if you are ready for this, or if you would rather keep things as they are.\nAre you prepared to be rid of this " + redMarker + "demon" + resetMarker + " once and for all?") {
      override def processCommand(command: String) = getBinaryAnswer(command)
    },

    "step back" -> new SimpleDialogueNode("This is too much. You let go of Fargoth's hand and rush towards the exit. As you step outside, you hear Fargoth say:\n\n" +
                                          " -- I understand, and I do not judge. But do not come ever back here.\nIf you can, seek isolation, so that when your mind eventually succumbs to " + redMarker + "his" + resetMarker + " influence, the people of this continent may be safe.\nMay Phasna help you in your journeys, Tormented One.\n"),
    "ending 1" -> new SimpleDialogueNode("\nYou leave the temple of Phasna, never to return here again. The rest of your life is spent wandering the continent, without goal, without purpose.\n" +
                                         "You continue seeking heroic deeds, until you feel that you are starting to lose the battle within you.\n" +
                                         "You then gear up for your final journey, and start walking north, into no-man's land. You keep walking until your consciousness fails you.\n" +
                                         "Your last memory is closing your eyes, and seeing the face of your tormentor, smiling in gleeful anticipation, as it takes control for good.") {
      override def reply() = {
        Game.isOver = true
        super.reply()
      }
    },
    "begin ritual" -> new SimpleDialogueNode(" -- Then let us begin.\n\nFargoth stands up, and lights several candles around the room.\nEach time he lights a candle, he mutters something under his breath - perhaps a prayer or perhaps some arcane words\n" +
                                             "After a few minutes the priest sits back down, takes you hand and says:\n\n" +
                                             "To sever your connection with " + redMarker + "him" + resetMarker + ", we will need to go through some of your most sacred memories together.\n" +
                                             "You must relive them and let go of them, those memories are the tethers " + redMarker + "he" + resetMarker + " uses to make your bond stronge.r\n" +
                                             "Have faith and do not waiver no matter what. With Phasna's blessing, these candles will protect us.\nRemember that if you turn away now, there will be no redemption.\n"),
    "first memory" -> new SimpleDialogueNode("For a moment your vision blurrs, and then suddenly you find yourself in a forest. You remember this fateful day vividly, even though you were but a youth back then...\n" +
                                             "It was storming, you and your little brother Farin are running through the forest, looking for some sort of shelter to escape the freezing rain and wind.\n" +
                                             "You hear Farin's voice:\n\n -- Look, brother, a cave! We can wait out the storm in there.\n\n" +
                                             "The two of you rush into the cave. At last, you're safe from the storm. But the rain and the wind do not subside.\n" +
                                             "To make the time pass faster, the two of you decide to explore the cave. Suprisingly, you find the cave to contain traces of civilization.\n" +
                                             "You spot glyphs and runes carved into the walls, various banches and urns made out of stone. You hear your brother's voice once again:\n\n" +
                                             " -- Hey, look over here, I've found something!\n\nYou look over to Farin and see him holding what looks to me a small knife with a beautiful ebony handle.\n" +
                                             "The knife mesmerizes you - it is perhaps the prettiest thing you have ever seen in your life. You want to have it.\n" +
                                             "You walk over to Farin and try to grab the knife from his hand.\nYour brother evades your grasp and exclaims:\n\n" +
                                             " -- Hey, quit it! I found it first, it's mine!\n\nYou reach out again, grabbing Farin's arm:\n\n" +
                                             " -- I am the older brother, so I should have it. - you say through your teeth as you are prying the knife from his fingers.\n\n" +
                                             " -- No way, you always get everything nice, this one is mine! - Farin retorts, but his grip is slipping.\n\n" +
                                             "At last you manage to free the knife from Farin's hand, but not before accidentally drawing a bit of blood from your finger with the blade.\n" +
                                             "At that moment, the blade flashes bright red, blinding you momentarily. As you come back to your senses, you feel the whole cave trembling.\n" +
                                             "Pieces of rock start falling from the cieling. You run towards the exit, but a loud cry makes you look back.\n" +
                                             "You see Farin pinned to the ground by a heavy boulder that landed on his leg.\nYou rush back, grab your brother's hand and pull as hard as you can, but the rock won't budge.\n" +
                                             "Suddenly you hear an unfamiliar voice:\n\n" + redMarker + " -- Hey, kiddo, want me to give you a hand in this? All you need to do is say \"yes\", and I will get you and your brother out of this cave." + resetMarker) {
      override def processCommand(command: String): String = {
        if (command.toLowerCase.contains("yes")) {
          Player.setChoice(0)
          "yes"
        } else {
          "no"
        }
      }
    },
    "save farin" -> new SimpleDialogueNode(redMarker + " -- Deal." + resetMarker + " - you hear in your head, and feel your muscles tense.\nYou pull once again, putting all the strength you had into it, " +
                                           "and with this pull, you manage to free you little brother.\n" +
                                           "That day you both managed to return home safely.\n"),
    "abandon farin" -> new SimpleDialogueNode("You ignore the voice and continue pulling, but your hands are still wet from the rain. You pulled and pulled, until you felt that Farin was no longer moving..."),
    "second stage" -> new SimpleDialogueNode("Your mind returns to the present. You hear Fargoth saying:\n\n -- We've unhooked the first tether, we have to keep going. Are you ready?") {
      override def processCommand(command: String) = getBinaryAnswer(command)
    },
    "second memory" -> new SimpleDialogueNode("Your vision blurrs once again. This time you find yourself in the middle of a battlefield, as you hear steel clashing against steel, and arrows whizzing by.\nThis memory is from the times of The Great Wars, you must have been in your twenties at the rime.\n" +
                                              "Your ambitions led you to enlisting as a recruit, your dreams back then revolved around becoming a renowned general.\n\n" +
                                              " -- Fall back! - you hear your commander shout - Retreat, lads, we cannot win this!\n\n" +
                                              "Anger stirrs within you. You want to attack, to rush into the enemy lines, slashing left and right with your sword, but the incompetence of your comrades is holding you back.\n" +
                                              "You hear the already familiar voice whisper to you:\n\n" + redMarker + " -- Hey, buddy, what do you say we win thing battle, eh?\nA simple \"yes\" from you, and I will give you enough power to turn this fight around all by yourself. Imagine all the glory!\n" + resetMarker) {
      override def processCommand(command: String): String = {
        if (command.toLowerCase.contains("yes")) {
          Player.setChoice(1)
          "yes"
        } else {
          "no"
        }
      }
    },
    "win battle" -> new SimpleDialogueNode("Demonic might surges within you, increasing your strength tenfold and hardening your skin against enemy strikes.\n" +
                                           "With the enemy crushed and scattered, your commander orders to press the attack, resulting in a decicive victory for your troops.\n" +
                                           "This battle put a start to your glorious military career, from which you retired with honors when The Great Wars subsided.\n"),
    "loose battle" -> new SimpleDialogueNode("The battle was lost the moment your commander ordered to retreat, there was no use risking your life.\n" +
                                             "You went on to build a middling military carreer, which you soon abandoned in favor of adventuring and pursuing heroic deeds around the continent.\n"),
    "third stage" -> new SimpleDialogueNode("You feel Fargoth clutching your hand and hear him say:\n\n -- We are almost there! ust one more memory, and I should be able to separate you from this " + redMarker + "fiend" + resetMarker + ".\n" +
                                            "Are you prepared to face for the final memory?") {
      override def processCommand(command: String) = getBinaryAnswer(command)
    },
    "third memory" -> new SimpleDialogueNode("You wake up on a bed in a small village hospital.\nYou remember this day well. That day you took on a vyvern that was terrorizing a local townspeople.\n" +
                                             "Your battle with the monster ended in a victory for you, but at dire costs - a blow the vyvern managed to land on your back with it's massive tail cracked your spine.\nYour entire lower body was in agonizing pain, and you could not move your legs.\n" +
                                             "You remember lying in bed and about how you are seemingly going to have to spend the rest of your life like this - half-paralyzed and miserable.\n" +
                                             "And then the oh-so-familiar voice was there again:\n\n" + redMarker + " -- Well, well, quite a pickle you have gotten yourself into, hm? Perhaps you need some help from your old pal?\n" +
                                             "As usual, a simple \"yes\" will be enough for me to get to work in fixing your back" + resetMarker) {
      override def processCommand(command: String): String = {
        if (command.toLowerCase.contains("yes")) {
          Player.setChoice(2)
          "yes"
        } else {
          "no"
        }
      }
    },
    "heal" -> new SimpleDialogueNode("And with with that \"yes\" your body was restored. You were able to walk away from your encounter with the vyvern unscathed.\n"),
                                     "dont heal" -> new SimpleDialogueNode("Over time, your body healed, several months after the incedent with the wyvern you were able to regain mobility.\n" + "" +
                                     "The pain, however, never went away. Every step you made since then reminded you of that battle with dull, unrelenting pain shooting through your spine.\n"),
    "finalee" -> new BinaryDialogueNode("You open your eyes - you are in the temple, this time for good.") {
      override def condition: Boolean = (Player.getChoiceSum == 3)
    },
    "no good" -> new SimpleDialogueNode("Fargoth looks up at you, sorrow and exhaustion in his eyes\n\n -- I am sorry, Tromented One.\n I have failed.\n" +
                                        "Your bond with the " + redMarker + "demon" + resetMarker + " has become co firm, that it is no longer possible to separate you.\n" +
                                        "I have done what I can, perhaps this will give you a few more years of sane existance, but " + redMarker + "he" + resetMarker + " will take you over eventually.\n" +
                                        "I recommend that when you feel that " + redMarker + "he" + resetMarker + " is getting close to overpowering your will, you should seek isolation.\n" +
                                        "Get as far away from the continent as you can, so that the people here are safe from the  " + redMarker + "demon's" + resetMarker + " wrath.\n" +
                                        "Now go, and may Phasna be with you on your travels."),
    "success" -> new BinaryDialogueNode("Fargoth looks up at you victoriously.\n\n -- We have done it, my friend. The fiend is no more.\nYou are free to live the rest of your life without the burden of sharing a mind with a demon.") {
      override def condition: Boolean = (Player.getChoiceSum > 0)
    },
    "ending 2" -> new SimpleDialogueNode("\nYou walk out of the temple, knowing that the " + redMarker + "he" + resetMarker + " is still here\n" +
    "Perhaps " + redMarker + "he" + resetMarker + " is quiet for now, but you know that he will return.\n" +
    "For now, you are free.\nYou have several years of joyful life ahead of you - your military renown ensures that you will always be get free drinks in any tavern or inn,\n bodily ailments will not bother you for a while and your brother will always be there to remenice about old times with.\n" +
    "Who knows how many years you will get? Maybe five, maybe tem, maybe even a couple decades.\n But the thought of all your misery coming inevitably coming back will haunt you forever more.") {
      override def reply() = {
        Game.isOver = true
        super.reply()
      }
    },
    "ending 3" -> new SimpleDialogueNode("\nYou walk out of the temple, take a breath of fresh autumn air, and for the first time in decades, feel truly free.\n" +
    "Some things you got to keep, some things you had to give up, but such is live, that we all must go through loss in one way or another,\n" +
    "and you feel at piece with that. Your mind is now your own, and only you get to decide how your story unfolds.") {
      override def reply() = {
        Game.isOver = true
        super.reply()
      }
    },
    "ending 4" -> new SimpleDialogueNode("\nYou walk out of the temple, at last free of your torment.\nYou have achieved your ultimate goal, reached your final destination, and yet the closure and relief you so desired is not there.\n" +
    "You've given up on your family, given up on your ambitions, invited pain into your life that made you into a resentful, cynical person.\n" + "" +
    "Perhaps, there is no closure? Perhaps you never get any \"compensation\" for how life treated you? Who knows...\n" +
    "At the very least you can say that whatever happens next is in your hands and in your hands alone.") {
      override def reply() = {
        Game.isOver = true
        super.reply()
      }
    }
  )

  private val branchesShorthand: Map[(String, String), String] = Map(
    (("start", "no")            -> "step back"),
    (("step back", "continue")            -> "ending 1"),
    (("start", "yes")            -> "begin ritual"),
    (("begin ritual", "continue") -> "first memory"),
    (("first memory", "yes") -> "save farin"),
    (("first memory", "no") -> "abandon farin"),
    (("save farin", "continue") -> "second stage"),
    (("abandon farin", "continue") -> "second stage"),
    (("second stage", "no") -> "step back"),
    (("second stage", "yes") -> "second memory"),
    (("second memory", "yes") -> "win battle"),
    (("second memory", "no") -> "loose battle"),
    (("win battle", "continue") -> "third stage"),
    (("loose battle", "continue") -> "third stage"),
    (("third stage", "no") -> "step back"),
    (("third stage", "yes") -> "third memory"),
    (("third memory", "yes") -> "heal"),
    (("third memory", "no") -> "dont heal"),
    (("heal", "continue") -> "finalee"),
    (("dont heal", "continue") -> "finalee"),
    (("finalee", "positiveResult") -> "no good"),
    (("finalee", "negativeResult") -> "success"),
    (("no good", "continue") -> "ending 2"),
    (("success", "positiveResult") -> "ending 3"),
    (("success", "negativeResult") -> "ending 4")
  )

  override val branches: Map[(DialogueNode, String), DialogueNode] = branchesShorthand.map( pair => (nodes(pair._1._1), pair._1._2) -> nodes(pair._2))

  override var currentNode = nodes("start")

  override def onStart(): String = currentNode.reply()

}

object Fargoth extends NPC("Fargoth") {

  override val location = TempleOfPhasna

  override val ds = FargothDS

}

