package xyz.teamgravity.coresdkandroid.result

private typealias RootError = Error

sealed class Result<out D, out E : RootError> {

    companion object {
        fun <E : RootError> success(): Result<Unit, E> {
            return Success(Unit)
        }
    }

    data class Success<out D, out E : RootError>(val data: D) : Result<D, E>()
    data class Error<out D, out E : RootError>(val error: E) : Result<D, E>()

    /**
     * Returns true if this instance represents a failed outcome.
     */
    val isFailure: Boolean get() = this is Error

    /**
     * Returns true if this instance represents a successful outcome.
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns the encapsulated error if this instance represents failure or null if it is success.
     */
    fun errorOrNull(): E? {
        return (this as? Error)?.error
    }

    /**
     * Returns the encapsulated value if this instance represents success or null if it is failure.
     */
    fun getOrNull(): D? {
        return (this as? Success)?.data
    }

    /**
     * Returns the result of onSuccess for the encapsulated value if this instance represents success or the result of onFailure function for
     * the encapsulated error if it is failure.
     */
    inline fun <R> fold(
        onSuccess: (data: D) -> R,
        onFailure: (error: E) -> R
    ): R {
        return when (this) {
            is Success -> onSuccess(this.data)
            is Error -> onFailure(this.error)
        }
    }

    /**
     * Calls onSuccess lambda if the result represents success. Calls onFailure lambda if the result represents failure.
     */
    inline fun onResult(
        onSuccess: (data: D) -> Unit,
        onFailure: (error: E) -> Unit
    ) {
        when (this) {
            is Success -> onSuccess(this.data)
            is Error -> onFailure(this.error)
        }
    }

    /**
     * Performs the given action on the encapsulated error if this instance represents failure.
     */
    inline fun onFailure(
        action: (error: E) -> Unit
    ) {
        if (this is Error) {
            action(this.error)
        }
    }

    /**
     * Performs the given action on the encapsulated value if this instance represents success.
     */
    inline fun onSuccess(
        action: (data: D) -> Unit
    ) {
        if (this is Success) {
            action(this.data)
        }
    }

    override fun toString(): String = when (val value = this) {
        is Success -> value.toString()
        is Error -> value.toString()
    }
}