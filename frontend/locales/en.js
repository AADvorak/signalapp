const en = {
  language: 'Language',
  name: 'English',
  common: {
    buttons: {
      open: 'Open',
      save: 'Save',
      create: 'Create',
      edit: 'Edit',
      delete: 'Delete',
      deleteWithSignals: 'Delete with signals',
      ok: 'OK',
      cancel: 'Cancel',
      process: 'Process',
      exportWav: 'Export wav',
      exportTxt: 'Export txt',
      exportSignal: 'Export signal',
      exportData: 'Export data',
      working: 'Working...',
      changePassword: 'Change password',
      signIn: 'Sign in',
      signUp: 'Sign up',
      signOut: 'Sign out',
      play: 'Play',
      stop: 'Stop',
      folders: 'Add to folders',
      clear: 'Clear',
      change: 'Change',
      toSignalManager: 'Go to signal manager',
      toSignalGenerator: 'Go to signal generator',
      continueWorkingWithSignal: 'Continue working with signal',
      askSelect: 'Show select dialog'
    },
    validation: {
      required: 'This field is required',
      positive: 'Must have positive value',
      greaterThanZero: 'Must be greater than zero',
      greaterThan: 'Must be greater than {minValue}',
      number: 'Must be a number',
      notGreaterThan: 'Must be not greater than {maxValue}',
      between: 'Must be between {minValue} and {maxValue}',
      same: 'Must have the same value'
    },
    messages: {
      fileSaveError: 'Error while saving file',
      processSignalWith: 'Process signal with {processorName}',
      processSignalsWith: 'Process signals with {processorName}',
      processed: 'Processed',
      nothingIsFound: 'Nothing is found. Change search criteria.',
      error: 'Error',
      wrongSignalSamplesNumber: 'Number of imported signal samples must be not greater than {maxSamplesNumber}. In selected file it is {samplesNumber}',
      noFolders: 'You have no folders',
      noData: 'No data',
      alreadySignedIn: 'Already signed in'
    },
    fields: {
      name: 'Name',
      description: 'Description',
      type: 'Type',
      email: 'Email',
      password: 'Password',
      passwordRepeat: 'Password repeat',
      oldPassword: 'Old password',
      firstName: 'First name',
      lastName: 'Last name',
      patronymic: 'Patronymic',
      search: 'Search',
      sampleRate: 'Sample rate',
      createTime: 'Create time',
      lastActionTime: 'Last action time',
      role: 'Role'
    },
    pagination: {
      total: 'Total: {pages} pages, {elements} entries',
      pageSize: 'Page size',
      loadParams: 'Load parameters'
    }
  },
  'confirm-dialog': {
    title: 'Confirmation'
  },
  'folder-editor': {
    title: 'Folder editor'
  },
  'message': {
    title: 'Message'
  },
  'select-dialog': {
    title: 'Selection',
    rememberSelection: 'Remember selection'
  },
  'select-processor': {
    title: 'Select processor',
    filterByTypes: 'Filter by types',
  },
  'default': {
    settings: 'Settings',
    user: 'User',
    navigation: 'Navigation',
    startPage: 'Start page',
    on: 'on',
    off: 'off',
    darkMode: 'Dark mode: {darkModeState}'
  },
  'number-input-type-select': {
    numberInputType: 'Number input',
    numberInputTypes: {
      text: 'Text',
      slider: 'Slider'
    }
  },
  'change-password': {
    name: 'Change password',
    passwordChangeSuccess: 'Password changed successfully',
    passwordChangeError: 'Error while changing password',
  },
  'email-confirm-error': {
    name: 'Email confirm error',
    goToUserSettings: 'Go to user settings'
  },
  'folder-manager': {
    name: 'Folder manager',
    confirmDeleteFolder: 'Are you sure to delete folder {name}?',
    saveError: 'Error while saving folder',
    deleteError: 'Error while deleting folder',
  },
  'restore-password': {
    name: 'Restore password',
    newPasswordSentByEmail: 'New password is sent by email',
    passwordSendError: 'Error while sending new password',
    restorePasswordMailTitle: 'SignalApp - restore password',
    restorePasswordMailMsg: 'Your new password for SignalApp: $newPassword$. It is recommended to change it immediately after sign in.',
  },
  'selects-settings': {
    afterSaveSignalActions: 'After save signal actions',
    importSignalActions: 'Actions with imported signal'
  },
  'signal': {
    name: 'Signal',
    saveAsNew: 'Save as new',
    sampleRate: 'Sample rate: {sampleRate} Hz',
    samplesNumber: 'Samples number: {samplesNumber}',
    processSignalError: 'Error while processing signal',
    signalDataSaveError: 'Error while saving signal data',
    signalSaveError: 'Error while saving signal',
    signalNotFound: 'Signal is not found',
    signalIsSaved: 'Signal is saved'
  },
  'signal-importer': {
    'import': 'Import',
    fromTextOrWavFile: 'From txt, csv, json, xml or wav file',
    importedFromFile: 'Imported from file {name}',
    selectAction: 'Select action with imported signal',
    selectChannel: 'Select audio channel to import',
    wrongFileFormat: 'Wrong file format',
  },
  'signal-generator': {
    name: 'Signal generator',
    generate: 'Generate',
    begin: 'Signal begin (B)',
    length: 'Signal length (L)',
    sampleRate: 'Signal sample rate (S)',
    frequency: 'Signal frequency (F)',
    amplitude: 'Signal amplitude (A)',
    offset: 'Signal offset (O)',
    form: 'Signal form',
    forms: {
      sine: 'sine',
      square: 'square',
      triangle: 'triangle',
      sawtooth: 'sawtooth',
      noise: 'noise'
    },
    signalName: 'Generated {form} signal',
    description: 'Generated {form} signal with F = {frequency} ({length} samples)',
    wrongPointsNumber: 'Number of samples (S * L) must be in range from 2 to 512000. Now it is {pointsNumber}',
    lessThanHalfSampleRate: 'Must be less than a half of sample rate',
    preview: 'Preview'
  },
  'signal-manager': {
    name: 'Signal manager',
    sampleRates: 'Sample rates',
    folders: 'Folders',
    youHaveNoStoredSignals: 'You have no stored signals',
    generate: 'Generate',
    or: 'or',
    record: 'record',
    newSignalsToStartWorking: 'new signals to start working',
    actionsWithSelectedSignals: 'Actions with selected signals',
    view: 'View',
    viewSignals: 'View signals',
    loadSignalsError: 'Error while loading signals',
    confirmDeleteSignal: 'Are you sure to delete {name}?',
    confirmDeleteSignals: 'Are you sure to delete selected {length} signals?',
  },
  'signal-recorder': {
    name: 'Signal recorder',
    sampleRate: 'Sample rate (Hz)',
    rec: 'REC',
    pause: 'Pause',
    stop: 'Stop',
    clear: 'Clear',
    recordStatuses: {
      ready: 'Ready to start record',
      recording: 'Recording...',
      paused: 'Recording paused',
    },
    nothingRecorded: 'Nothing is recorded',
    audioFormat: 'Format: 1 channel, pcm, {sampleRate} Hz',
    fileName: 'Recorded {dateTime}.wav',
  },
  'signin': {
    name: 'Sign in',
    signInToContinue: 'Please sign in to continue',
    forgotPassword: 'Forgot password',
    signInError: 'Error while sign in',
  },
  'signup': {
    name: 'Sign up',
    signUpError: 'Error while sign up'
  },
  'user-settings': {
    name: 'User settings',
    emailNotConfirmed: 'Email is not confirmed',
    emailConfirmed: 'Email is confirmed',
    confirm: 'Confirm',
    deleteAccount: 'Delete account',
    saveSuccess: 'User info saved',
    saveError: 'Error while saving user info',
    deleteConfirm: 'Are you sure to delete your account? All your data will be removed immediately with no possibility of recover.',
    deleteError: 'Error while deleting account',
    sendEmailError: 'Error while sending email',
    confirmSentCheckEmail: 'Confirm message is sent, check your email {email}',
    confirmEmailMailTitle: 'SignalApp - confirm email',
    confirmEmailMailMsg: 'To confirm you email use the link $origin$/api/users/confirm/$code$'
  },
  'admin-users': {
    name: 'Users',
    roles: 'Roles',
    confirmDeleteUser: 'Are you sure to delete user with email {email}?'
  },
  processorNames: {
    LinearAmp: 'Linear amplifier',
    withLinearAmp: 'with linear amplifier',
    PiecewiseLinearSymmetricSaturationAmp: 'Piecewise linear symmetric amplifier with saturation',
    withPiecewiseLinearSymmetricSaturationAmp: 'with piecewise linear symmetric amplifier with saturation',
    PiecewiseLinearAsymmetricSaturationAmp: 'Piecewise linear asymmetric amplifier with saturation',
    withPiecewiseLinearAsymmetricSaturationAmp: 'with piecewise linear asymmetric amplifier with saturation',
    Inverter: 'Inverter',
    withInverter: 'with inverter',
    AmplitudeModulator: 'Amplitude modulator',
    withAmplitudeModulator: 'with amplitude modulator',
    FrequencyModulator: 'Frequency modulator',
    withFrequencyModulator: 'with frequency modulator',
    LpRcFilter: 'Low-pass RC filter',
    withLpRcFilter: 'with low-pass RC filter',
    HpRcFilter: 'High-pass RC filter',
    withHpRcFilter: 'with high-pass RC filter',
    LinearOscillator: 'Linear oscillator',
    withLinearOscillator: 'with linear oscillator',
    Integrator: 'Integrator',
    withIntegrator: 'with integrator',
    Differentiator: 'Differentiator',
    withDifferentiator: 'with differentiator',
    SpectrumAnalyserDct: 'Spectrum analyser (DCT)',
    withSpectrumAnalyserDct: 'with spectrum analyser (DCT)',
    SpectrumAnalyserFft: 'Spectrum analyser (FFT)',
    withSpectrumAnalyserFft: 'with spectrum analyser (FFT)',
    SelfCorrelator: 'Autocorrelator',
    withSelfCorrelator: 'with Autocorrelator',
    Adder: 'Adder',
    withAdder: 'with adder',
    Correlator: 'Correlator',
    withCorrelator: 'with correlator',
    TwoSignalAmplitudeModulator: 'Two signal amplitude modulator (carrier, modulating)',
    withTwoSignalAmplitudeModulator: 'with two signal amplitude modulator (carrier, modulating)'
  },
  processorTypes: {
    amplifier: 'Amplifier',
    modulator: 'Modulator',
    filter: 'Filter',
    oscillator: 'Oscillator',
    math: 'Math',
  },
  processorParams: {
    Adder: {
      coefficient1: 'Signal 1 coefficient',
      coefficient2: 'Signal 2 coefficient',
      description: 'Sum of {name1} with coefficient {coefficient1} and {name2} with coefficient {coefficient2}',
    },
    AmplitudeModulator: {
      frequency: 'Carrier frequency',
      amplitude: 'Carrier amplitude',
      depth: 'Modulation depth',
    },
    Correlator: {
      description: 'Correlation of {name1} and {name2}',
    },
    FrequencyModulator: {
      frequency: 'Carrier frequency',
      amplitude: 'Carrier amplitude',
      coefficient: 'Modulation index',
    },
    HpRcFilter: {
      tau: 'Time constant',
    },
    LinearAmp: {
      coefficient: 'Amplification coefficient'
    },
    LinearOscillator: {
      frequency: 'Frequency',
      damping: 'Damping',
    },
    LpRcFilter: {
      tau: 'Time constant',
    },
    PiecewiseLinearAsymmetricSaturationAmp: {
      coefficient: 'Amplification coefficient',
      maxPositiveOutput: 'Max positive output amplitude',
      maxNegativeOutput: 'Max negative output amplitude',
    },
    PiecewiseLinearSymmetricSaturationAmp: {
      coefficient: 'Amplification coefficient',
      maxOutput: 'Max output amplitude',
    },
    TwoSignalAmplitudeModulator: {
      depth: 'Modulation depth',
      description: '{name1} with amplitude modulated by {name2} with modulation depth {depth}'
    }
  },
  signalNames: {
    signal1: 'Signal 1',
    signal2: 'Signal 2',
    carrierSignal: 'Carrier signal',
    modulatingSignal: 'Modulating signal'
  },
  operationNames: {
    preparingSignal: 'Preparing signal',
    preparingSignals: 'Preparing signals',
    calculating: 'Calculating',
    calculatingMaxAbsValue: 'Post processing',
  },
  serverMessages: {
    SIGNAL_DOES_NOT_EXIST: 'Signal does not exist',
    EMAIL_ALREADY_EXISTS: 'Email already exists',
    FOLDER_NAME_ALREADY_EXISTS: 'Folder name already exists',
    EMAIL_ALREADY_CONFIRMED: 'Email is already confirmed',
    WRONG_EMAIL_PASSWORD: 'Wrong email and password pair',
    WRONG_EMAIL: 'Email does not exist or is not confirmed',
    WRONG_OLD_PASSWORD: 'Wrong old password',
    TOO_LONG_FILE: 'Too long file. Max size is {maxSize} bytes.',
    TOO_LONG_SIGNAL: 'Too long signal. Max size is {maxSize} samples.',
    TOO_MANY_SIGNALS_STORED: 'Too many signals are stored. Max number is {maxNumber} signals.',
    TOO_MANY_FOLDERS_CREATED: 'Too many folders are created. Max number is {maxNumber} folders.',
    NotEmpty: 'This field is required',
    MinLength: 'Min length violation',
    Size: 'Size violation',
    MaxLength: 'Max length violation',
    Email: 'Must be in the format of email',
    RECAPTCHA_TOKEN_NOT_VERIFIED: 'Recaptcha verification failed',
    INTERNAL_SERVER_ERROR: 'Internal server error',
    UNKNOWN_ERROR: 'Unknown error'
  },
  chart: {
    resetZoom: 'Reset zoom'
  }
}

export default en
