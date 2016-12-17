/**
 * Runs the given function over and over again. The run function is passed
 * a callback so that it can signal finish or error. In case of error streaks
 * the delay between individual reruns will increase.
 *
 * If the run functions returns a value, it must be a function that allows to
 * cancel the current run.
 *
 * @param run The function to rerun
 * @param baseDelay Basic rerun delay for the exponential backoff
 * @param maxDelay Maximum rerun delay for the exponential backoff
 *
 * @returns {stop} A function that can be called to stop the backoff loop
 */
export default function exponentialBackoff (run, baseDelay = 100, maxDelay = 60000) {
  let retryTimeout
  let cancelRun
  let stopped = false

  const handler = (errorCount) => {
    let delay = Math.min(Math.round(Math.random() * (Math.pow(2, errorCount, 10) - 1)) * baseDelay, maxDelay)

    retryTimeout = window.setTimeout(() => {
      retryTimeout = null
      let done = false

      cancelRun = run((err) => {
        if (!done) {
          done = true
          cancelRun = null

          if (!stopped) {
            // reset or increase error count depeding on err
            handler(!err ? 0 : errorCount + 1)
          }
        }
      })
    }, delay)
  }
  handler(0)

  return function stop () {
    stopped = true

    if (retryTimeout !== null) {
      window.clearTimeout(retryTimeout)
      retryTimeout = null
    }

    if (cancelRun) {
      cancelRun()
      cancelRun = null
    }
  }
}
