import scala.collection.mutable

object Player {

  private var gold = 12
  private var backpack= Map[String, Item]("staff" -> Staff)
  private var choices = mutable.Buffer(false, false, false)

  def givePlayerGold(amount: Int) = {
    this.gold += amount
  }

  def playerGold = this.gold

  def takePlayerGold(amount: Int) = {
    this.gold = Math.max(this.gold - amount, 0)
  }

  def hasItem(itemName: String) = backpack.contains(itemName)

  def removeItem(itemName: String) = {
    backpack -= itemName
  }

  def setChoice(index: Int) = choices(index) = true

  def getChoiceSum = choices.count( _ == true )

  def getInventory = s"Gold: $playerGold \n" + backpack.keys.mkString("\n")

  def getItemsInBakcpack = backpack.keys

  def getItemDescription(itemName: String) = backpack(itemName).description


}
