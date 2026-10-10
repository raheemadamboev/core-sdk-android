package xyz.teamgravity.coresdkandroid.result


data class MessageError(
    val errors: List<String>
) : Error {
    companion object {
        fun <D> create(errors: List<String> = emptyList()): Result<D, MessageError> {
            return Result.Error(MessageError(errors))
        }
    }
}