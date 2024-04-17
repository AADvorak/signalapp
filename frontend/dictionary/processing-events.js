export const ProcessingEvents = {
  /**
   * triggered when user inputs new values of processor parameters, which can be wrong
   */
  VALIDATION_FAILED: 'validationFailed',
  /**
   * triggered when validation of processor parameters is successfully passed
   */
  VALIDATION_PASSED: 'validationPassed',
  /**
   * triggered when processing progress is changed, has argument - new value of progress
   */
  PROGRESS_CHANGED: 'progressChanged',
  /**
   * triggered when user has selected a processor for signal
   */
  PROCESSOR_SELECTED: 'processorSelected',
  /**
   * triggered when signal processing is started
   */
  PROCESSING_STARTED: 'processingStarted',
  /**
   * triggered when signal processing is finished (either successfully or with error)
   */
  PROCESSING_FINISHED: 'processingFinished',
  /**
   * triggered when error occurred while signal processing, has argument - error
   */
  PROCESSING_ERROR: 'processingError',
  /**
   * triggered when it's time to start signal processing
   */
  PROCESS: 'process',
  /**
   * triggered when user canceled signal processing
   */
  CANCEL: 'cancel'
}
