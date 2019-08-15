package hu.zeletrik.dsmtimer.domain

data class RetroItem(
     val time: String,
     val text: String
) {
     override fun toString(): String {
          return "$time - $text"
     }
}