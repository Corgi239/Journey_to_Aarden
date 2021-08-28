Hey! This is the official guide to my game.

To run the game, download the Journey_to_Aarden.jar file (located at out/artifacts/Journey_to_Aarden_jar), and then run it with the following command line prompt:

java -jar <location of the jar Journey_to_Aarden.jar file>


The guide is divided into two parts: a walkthrough and a technical explanation of how the code works. I will start with the walkthrough.


1. Walkthrough.


                                               (mysterious path)
                                                    RIGHT
                                                      ^
                                                      |
                                                      |             (temple of Phasna)
MAP:            RIVERSIDE -----> PIER ------------> AARDEN -------------> TEMPLE
                                              (gates of Aarden)


The game starts at the riverside, from which you can go directly to the pier. There you can have a chat with the fisherman.
If you ask the fisherman a phrase that contains "Aarden", "directions" or "city", he'll tell you how to get to Aarden.
You can also ask him about his fishing tales, and he we will tell you an insightful story that actually spoils the endings!

From there you can make your way to the Gates of Aarden. Here your main obective is to get through the guard.
But firts you need to wake him up - use a "!" or caps. Now you need to get the guard to let you in.
There are two ways to approach that:
1) You can sell your staff to the female merchant at the Gates of Aarden and pay for your entrance that way. to do this: "talk to woman" -> "yes" -> "exit" -> "talk to guard"
At this point the guard will either let you in right away, or, if you have talked to him before, you need to mention "tax" or "gold" to him, and he will take your payment.
2) You can go right, through the mysterious path, and find a celebration in progress. If you talk to the elf, he will invite you to drink with them.
If you do, another partygoer will tell you how to get through the guard without paying - you need to mention "tradition" or "custom" to the guard, he will then give you a riddle.
Giving the answer that you recieved from the partygoer should do the trick. But beware - if you drink with the partying folk, they will snatch some gold off you!

Now you are headed to the temple. There you need to talk with Fargoth.
Fargoth will go through a few of your memories, where you can decide to either enlist the help of the demon or refuse him. The ending depends on these choices

To get ending 1, tell Fargoth that you're not ready at any point.
To get ending 2, accept the demon's help every time
To get ending 3, accept the demon's help once or twice
To get ending 4, accept the demons's help 3 times.

This is pretty much it.

2. Technical comment

There are a few key decisions I made that changed the way the game works compared to the template adventure offered in the course materials.
First of all, I got rid of the Action class. In hindsight, I could have kept it, but imo it forces all locations to feel kinda same-y, which I ideally would like to avoid.
Second of all, I introduced a dialogue system. Each instance of the dialogue system contains a bunch of dialogue nodes. Each node knows a piece of dialogue and what kind of commands can be expected from
the player at a specific point in dialogue. All nodes have an processCommand method, that can be overwritten to make every node feel more unique. The dialogue system then controls
navigation between the nodes.

Dialogue systems and Areas are united under the trait Scene, which allows the UI to treat them in the same way.

I am still a bit confused about the packages in IDEA, so all the NPCs and their dialogue systems are located in the same file. Same for locations.
I labeled them with comments to make code navigation a tad easier.

Overall, I am quite happy with how the game turned out, I hope the freedom of inputs in dialogues makes the game world feel a bit more alive and immersive.
There are a couple features that I did not have time to implement, but the core is all there.
