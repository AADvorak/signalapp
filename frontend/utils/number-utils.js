const NumberUtils = {

  reduceFractionDigitsByValue(value) {
    let fractionDigits = 0
    if (value > 1000) {
      fractionDigits = 0
    } else if (value > 100) {
      fractionDigits = 1
    } else if (value > 10) {
      fractionDigits = 2
    } else if (value > 1) {
      fractionDigits = 3
    } else {
      fractionDigits = 4
    }
    return value.toFixed(fractionDigits)
  }

}

export default NumberUtils
