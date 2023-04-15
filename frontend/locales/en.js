import {dataStore} from "~/stores/data-store";

const en = {
	language: 'Language',
	name: 'English',
	common: {
		buttons: {
			save: 'Save',
			delete: 'Delete',
			ok: 'OK',
			cancel: 'Cancel',
			transform: 'Transform',
			exportWav: 'Export wav',
			exportTxt: 'Export txt',
			working: 'Working...',
			changePassword: 'Change password',
			signIn: 'Sign in',
			signUp: 'Sign up',
			signOut: 'Sign out'
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
			transformSignalWith: 'Transform signal with {transformerName}',
			transformSignalsWith: 'Transform signals with {transformerName}',
			transformed: 'Transformed'
		},
		fields: {
			name: 'Name',
			description: 'Description',
			type: 'Type',
			email: 'Email',
			password: 'Password',
			passwordRepeat: 'Password repeat',
			firstName: 'First name',
			lastName: 'Last name',
			patronymic: 'Patronymic'
		},
		search: 'Search'
	},
	'confirm-dialog': {
		title: 'Confirmation'
	},
	'message': {
		title: 'Message'
	},
	'select-transformer-dialog': {
		title: 'Select transformer',
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
	'change-password': {
		name: 'Change password',
		oldPassword: 'Old password',
		passwordChangeSuccess: 'Password changed successfully',
		passwordChangeError: 'Error while changing password',
	},
	'email-confirm-error': {
		name: 'Email confirm error',
		goToUserSettings: 'Go to user settings'
	},
	'restore-password': {
		name: 'Restore password',
		newPasswordSentByEmail: 'New password is sent by email',
		passwordSendError: 'Error while sending new password',
	},
	'signal': {
		name: 'Signal editor',
		saveAsNew: 'Save as new',
		sampleRate: 'Sample rate = {sampleRate}'
	},
	'signal-generator': {
		name: 'Signal generator',
		generate: 'Generate',
		begin: 'Signal begin',
		length: 'Signal length',
		sampleRate: 'Signal sample rate',
		frequency: 'Signal frequency',
		amplitude: 'Signal amplitude',
		offset: 'Signal offset',
		form: 'Signal form',
		forms: {
			sine: 'sine',
			square: 'square',
			triangle: 'triangle',
			sawtooth: 'sawtooth',
			noise: 'noise'
		},
		signalName: 'Generated {form} signal',
		description: 'Generated {form} signal with F = {frequency} ({length} points)',
		'import': 'Import',
		fromTxtOrWavFile: 'From txt or wav file',
		importedFromFile: 'Imported from file {name}',
		wrongPointsNumber: 'Number of signal points (S * L) must be in range from 2 to 512000. Now it is {pointsNumber}',
		lessThanHalfSampleRate: 'Must be less than a half of sample rate'
	},
	'signal-manager': {
		name: 'Signal manager',
		total: 'Total: {pages} pages, {elements} entries',
		pageSize: 'Page size',
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
		confirmDeleteSignals: 'Are you sure to delete selected {length} signals?'
	},
	'signal-recorder': {
		name: 'Signal recorder',
		sampleRateHz: 'Sample rate (Hz)',
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
		signInError: 'Error while sign in'
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
		confirmSentCheckEmail: 'Confirm message is sent, check your email {email}'
	},
	transformerNames: {
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
		SpectrumAnalyser: 'Spectrum analyser',
		withSpectrumAnalyser: 'with spectrum analyser',
		SelfCorrelator: 'Self correlator',
		withSelfCorrelator: 'with self correlator',
		Adder: 'Adder',
		withAdder: 'with adder',
		Correlator: 'Correlator',
		withCorrelator: 'with correlator',
		TwoSignalAmplitudeModulator: 'Two signal amplitude modulator (carrier, modulating)',
		withTwoSignalAmplitudeModulator: 'with two signal amplitude modulator (carrier, modulating)'
	},
	transformerTypes: {
		amplifier: 'Amplifier',
		modulator: 'Modulator',
		filter: 'Filter',
		oscillator: 'Oscillator',
		math: 'Math',
	},
	transformerParams: {
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
			deviation: 'Frequency deviation',
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
		preparingSignals: 'Preparing signals',
		estimating: 'Estimating',
		calculatingMaxAbsValue: 'Calculating max abs value',
	}
}

export default en
