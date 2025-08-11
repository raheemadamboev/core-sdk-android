package xyz.teamgravity.coresdkandroid.result

enum class GeneralError : Error {

    /**
     * Indicates an error returned by a remote server.
     * This could be due to various server-side issues like internal server errors,
     * bad requests processed by the server, or service unavailability.
     */
    Server,

    /**
     * Signifies that an operation was attempted on an object or component
     * that was not in an appropriate state to perform that operation.
     * For example, trying to use an uninitialized component.
     */
    IllegalState,

    /**
     * Denotes that a method was called with an invalid or inappropriate argument.
     * This often means a precondition for a method call was not met.
     */
    IllegalArgument,

    /**
     * Indicates that a requested resource, object, or piece of data could not be found.
     * This is common in scenarios like fetching data by ID where the ID does not exist.
     */
    NotFound,

    /**
     * Represents an error that occurred during the process of serializing data
     * (converting an object into a format for storage or transmission) or
     * deserializing data (converting it back into an object).
     */
    Serialization;

    companion object {
        fun <D> server(): Result<D, GeneralError> {
            return Result.Error(Server)
        }

        fun <D> illegalState(): Result<D, GeneralError> {
            return Result.Error(IllegalState)
        }

        fun <D> illegalArgument(): Result<D, GeneralError> {
            return Result.Error(IllegalArgument)
        }

        fun <D> notFound(): Result<D, GeneralError> {
            return Result.Error(NotFound)
        }

        fun <D> serialization(): Result<D, GeneralError> {
            return Result.Error(Serialization)
        }
    }
}