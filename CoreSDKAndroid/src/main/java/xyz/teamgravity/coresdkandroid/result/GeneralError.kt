package xyz.teamgravity.coresdkandroid.result

/**
 * General reasons an operation can fail, shared by every Gravity app and library.
 *
 * The first six values are general. The rest describe failures of a call to a remote server; core-sdk-network maps
 * HTTP status codes and network exceptions to them. New values are always added at the end, so existing values keep
 * their position.
 */
enum class GeneralError : Error {

    /**
     * Signifies that an operation could not be completed due to a lack of active internet connectivity
     * on the device. The attempted call requires an internet connection to reach a remote service
     * or resource, but no such connection was detected.
     *
     * This error typically means the network request was not even attempted because the system
     * reported no available network, or the server's host name could not be resolved.
     */
    Internet,

    /**
     * Indicates an error on the remote server's side.
     * This could be due to various server-side issues like internal server errors
     * or service unavailability. For HTTP calls, any 5xx status code.
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
     * For HTTP calls, the server rejected the request as malformed (400 Bad Request).
     */
    IllegalArgument,

    /**
     * Indicates that a requested resource, object, or piece of data could not be found.
     * This is common in scenarios like fetching data by ID where the ID does not exist.
     * For HTTP calls, 404 Not Found.
     */
    NotFound,

    /**
     * Represents an error that occurred during the process of serializing data
     * (converting an object into a format for storage or transmission) or
     * deserializing data (converting it back into an object).
     * For HTTP calls, the response body did not match the expected type.
     */
    Serialization,

    /**
     * Indicates that an operation did not finish within its allowed time.
     * For HTTP calls, the request or the socket timed out on the device,
     * or the server answered 408 Request Timeout.
     */
    Timeout,

    /**
     * Indicates that the caller is not authenticated, or its credentials are missing, invalid or expired.
     * Signing in again or refreshing the token usually resolves it.
     * For HTTP calls, 401 Unauthorized.
     */
    Unauthorized,

    /**
     * Indicates that the caller is authenticated but is not allowed to perform the operation
     * or access the resource. Signing in again does not change the outcome.
     * For HTTP calls, 403 Forbidden.
     */
    Forbidden,

    /**
     * Indicates that the operation conflicts with the current state of the resource.
     * For example, creating something that already exists or updating data that changed in the meantime.
     * For HTTP calls, 409 Conflict.
     */
    Conflict,

    /**
     * Indicates that the data sent with the operation is larger than the receiver accepts.
     * For example, uploading a file over the server's size limit.
     * For HTTP calls, 413 Payload Too Large.
     */
    PayloadTooLarge,

    /**
     * Indicates that too many operations were sent in a given amount of time and the receiver
     * is limiting them. Retrying later, ideally with a delay, usually succeeds.
     * For HTTP calls, 429 Too Many Requests.
     */
    TooManyRequests,

    /**
     * Represents a failure that does not match any other value.
     * For HTTP calls, any status code without a dedicated value, or an unexpected exception.
     */
    Unknown;

    companion object {

        /**
         * Returns a failed result with [Internet].
         */
        fun <D> internet(): Result<D, GeneralError> {
            return Result.Error(Internet)
        }

        /**
         * Returns a failed result with [Server].
         */
        fun <D> server(): Result<D, GeneralError> {
            return Result.Error(Server)
        }

        /**
         * Returns a failed result with [IllegalState].
         */
        fun <D> illegalState(): Result<D, GeneralError> {
            return Result.Error(IllegalState)
        }

        /**
         * Returns a failed result with [IllegalArgument].
         */
        fun <D> illegalArgument(): Result<D, GeneralError> {
            return Result.Error(IllegalArgument)
        }

        /**
         * Returns a failed result with [NotFound].
         */
        fun <D> notFound(): Result<D, GeneralError> {
            return Result.Error(NotFound)
        }

        /**
         * Returns a failed result with [Serialization].
         */
        fun <D> serialization(): Result<D, GeneralError> {
            return Result.Error(Serialization)
        }

        /**
         * Returns a failed result with [Timeout].
         */
        fun <D> timeout(): Result<D, GeneralError> {
            return Result.Error(Timeout)
        }

        /**
         * Returns a failed result with [Unauthorized].
         */
        fun <D> unauthorized(): Result<D, GeneralError> {
            return Result.Error(Unauthorized)
        }

        /**
         * Returns a failed result with [Forbidden].
         */
        fun <D> forbidden(): Result<D, GeneralError> {
            return Result.Error(Forbidden)
        }

        /**
         * Returns a failed result with [Conflict].
         */
        fun <D> conflict(): Result<D, GeneralError> {
            return Result.Error(Conflict)
        }

        /**
         * Returns a failed result with [PayloadTooLarge].
         */
        fun <D> payloadTooLarge(): Result<D, GeneralError> {
            return Result.Error(PayloadTooLarge)
        }

        /**
         * Returns a failed result with [TooManyRequests].
         */
        fun <D> tooManyRequests(): Result<D, GeneralError> {
            return Result.Error(TooManyRequests)
        }

        /**
         * Returns a failed result with [Unknown].
         */
        fun <D> unknown(): Result<D, GeneralError> {
            return Result.Error(Unknown)
        }
    }
}
