trait Scene{

def onStart():String

def execute(command:String):String

def onExit():String

def getHelpInfo: String

}
