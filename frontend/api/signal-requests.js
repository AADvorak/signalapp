import ApiProvider from "~/api/api-provider";
import WavCoder from "~/audio/wav-coder";

export const SignalRequests = {
  apiProvider: ApiProvider,
  setApiProvider(apiProvider) {
    this.apiProvider = apiProvider
  },
  async loadSignalWithData(signalId) {
    let signal, data
    const loadSignal = async () => signal = await this.loadSignal(signalId)
    const loadData = async () => data = await this.loadData(signalId)
    await Promise.all([loadSignal(), loadData()])
    if (signal && data) {
      signal.data = data.map(value => value * signal.maxAbsY)
      return signal
    }
  },
  async loadSignal(signalId) {
    const response = await this.apiProvider.get(`/api/signals/${signalId}`)
    if (response.ok) {
      return response.data
    } else {
      throw response
    }
  },
  async loadData(signalId) {
    const response = await this.apiProvider.get(`/api/signals/${signalId}/wav`)
    if (response.ok) {
      return (await WavCoder.wavToSignals(response.data))[0].data
    } else {
      throw response
    }
  },
  saveNewSignalDtoWithData(signalDto, data) {
    return this.apiProvider.postMultipart('/api/signals', signalDto, data)
  },
  saveNewSignal(signal) {
    return this.saveNewSignalDtoWithData(this.makeSignalDto(signal), WavCoder.signalToWav(signal))
  },
  saveExistingSignal(signal) {
    return this.apiProvider.putMultipart(`/api/signals/${signal.id}`, this.makeSignalDto(signal), WavCoder.signalToWav(signal))
  },
  makeSignalDto(signal) {
    return {
      name: signal.name,
      description: signal.description,
      maxAbsY: signal.maxAbsY,
      xMin: signal.xMin,
      sampleRate: signal.sampleRate,
    }
  }
}
