abstract class Item(val name: String) {
  val description: String
}

object Staff extends Item("staff") {
  override val description = "A beautifull oaken staff, engraved with magical runes and decorated with a blue ribbon.\nDuring your long journeys, you've had master-enchanters from all over the continent engrave their most powerful runes along the staff.\nYou've grown to rely on this staff, both for easing your travels and for defending your against foes, it practically feels like an extention of your body."
}