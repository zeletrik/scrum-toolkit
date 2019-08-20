package hu.zeletrik.scrumtoolkit.domain

data class RetroItem(
     val time: String,
     val text: String
) {
     override fun toString(): String {
          return "$time - $text"
     }
}