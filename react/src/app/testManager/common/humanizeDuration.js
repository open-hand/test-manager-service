

(function () {
  const languages = {
    en: {
      y(c) { return `year${c === 1 ? '' : 's'}`; },
      mo(c) { return `month${c === 1 ? '' : 's'}`; },
      w(c) { return `week${c === 1 ? '' : 's'}`; },
      d(c) { return `day${c === 1 ? '' : 's'}`; },
      h(c) { return `hour${c === 1 ? '' : 's'}`; },
      m(c) { return `minute${c === 1 ? '' : 's'}`; },
      s(c) { return `second${c === 1 ? '' : 's'}`; },
      ms(c) { return `millisecond${c === 1 ? '' : 's'}`; },
      decimal: '.',
    },
    zh_CN: {
      y: '年',
      mo: '个月',
      w: '周',
      d: '天',
      h: '小时',
      m: '分钟',
      s: '秒',
      ms: '毫秒',
      decimal: '.',
    },
  };

  // You can create a humanizer, which returns a function with default
  // parameters.
  function humanizer(passedOptions) {
    var result = function humanizer(ms, humanizerOptions) {
      const options = extend({}, result, humanizerOptions || {});
      return doHumanization(ms, options);
    };

    return extend(result, {
      language: 'en',
      delimiter: ', ',
      spacer: ' ',
      conjunction: '',
      serialComma: true,
      units: ['y', 'mo', 'w', 'd', 'h', 'm', 's'],
      languages: {},
      round: false,
      unitMeasures: {
        y: 31557600000,
        mo: 2629800000,
        w: 604800000,
        d: 86400000,
        h: 3600000,
        m: 60000,
        s: 1000,
        ms: 1,
      },
    }, passedOptions);
  }

  // The main function is just a wrapper around a default humanizer.
  const humanizeDuration = humanizer({});

  // doHumanization does the bulk of the work.
  function doHumanization(ms, options) {
    let i; let len; let piece;

    // Make sure we have a positive number.
    // Has the nice sideffect of turning Number objects into primitives.
    ms = Math.abs(ms);

    const dictionary = options.languages[options.language] || languages[options.language];
    if (!dictionary) {
      throw new Error(`No language ${dictionary}.`);
    }

    const pieces = [];

    // Start at the top and keep removing units, bit by bit.
    let unitName; let unitMS; let 
      unitCount;
    for (i = 0, len = options.units.length; i < len; i++) {
      unitName = options.units[i];
      unitMS = options.unitMeasures[unitName];

      // What's the number of full units we can fit?
      if (i + 1 === len) {
        unitCount = ms / unitMS;
      } else {
        unitCount = Math.floor(ms / unitMS);
      }

      // Add the string.
      pieces.push({
        unitCount,
        unitName,
      });

      // Remove what we just figured out.
      ms -= unitCount * unitMS;
    }

    let firstOccupiedUnitIndex = 0;
    for (i = 0; i < pieces.length; i++) {
      if (pieces[i].unitCount) {
        firstOccupiedUnitIndex = i;
        break;
      }
    }

    if (options.round) {
      let ratioToLargerUnit; let 
        previousPiece;
      for (i = pieces.length - 1; i >= 0; i--) {
        piece = pieces[i];
        piece.unitCount = Math.round(piece.unitCount);

        if (i === 0) { break; }

        previousPiece = pieces[i - 1];

        ratioToLargerUnit = options.unitMeasures[previousPiece.unitName] / options.unitMeasures[piece.unitName];
        if ((piece.unitCount % ratioToLargerUnit) === 0 || (options.largest && ((options.largest - 1) < (i - firstOccupiedUnitIndex)))) {
          previousPiece.unitCount += piece.unitCount / ratioToLargerUnit;
          piece.unitCount = 0;
        }
      }
    }

    const result = [];
    for (i = 0, pieces.length; i < len; i++) {
      piece = pieces[i];
      if (piece.unitCount) {
        result.push(render(piece.unitCount, piece.unitName, dictionary, options));
      }

      if (result.length === options.largest) { break; }
    }

    if (result.length) {
      if (!options.conjunction || result.length === 1) {
        return result.join(options.delimiter);
      } else if (result.length === 2) {
        return result.join(options.conjunction);
      } else if (result.length > 2) {
        return result.slice(0, -1).join(options.delimiter) + (options.serialComma ? ',' : '') + options.conjunction + result.slice(-1);
      }
    } else {
      return render(0, options.units[options.units.length - 1], dictionary, options);
    }
  }

  function render(count, type, dictionary, options) {
    let decimal;
    if (options.decimal === void 0) {
      decimal = dictionary.decimal;
    } else {
      decimal = options.decimal;
    }

    const countStr = count.toString().replace('.', decimal);

    const dictionaryValue = dictionary[type];
    let word;
    if (typeof dictionaryValue === 'function') {
      word = dictionaryValue(count);
    } else {
      word = dictionaryValue;
    }

    return countStr + options.spacer + word;
  }

  function extend(destination) {
    let source;
    for (let i = 1; i < arguments.length; i++) {
      source = arguments[i];
      for (const prop in source) {
        if (source.hasOwnProperty(prop)) {
          destination[prop] = source[prop];
        }
      }
    }
    return destination;
  }

  // Internal helper function for Polish language.
  function getPolishForm(c) {
    if (c === 1) {
      return 0;
    } else if (Math.floor(c) !== c) {
      return 1;
    } else if (c % 10 >= 2 && c % 10 <= 4 && !(c % 100 > 10 && c % 100 < 20)) {
      return 2;
    } else {
      return 3;
    }
  }

  // Internal helper function for Russian and Ukranian languages.
  function getSlavicForm(c) {
    if (Math.floor(c) !== c) {
      return 2;
    } else if ((c % 100 >= 5 && c % 100 <= 20) || (c % 10 >= 5 && c % 10 <= 9) || c % 10 === 0) {
      return 0;
    } else if (c % 10 === 1) {
      return 1;
    } else if (c > 1) {
      return 2;
    } else {
      return 0;
    }
  }

  // Internal helper function for Slovak language.
  function getCzechOrSlovakForm(c) {
    if (c === 1) {
      return 0;
    } else if (Math.floor(c) !== c) {
      return 1;
    } else if (c % 10 >= 2 && c % 10 <= 4 && c % 100 < 10) {
      return 2;
    } else {
      return 3;
    }
  }

  // Internal helper function for Lithuanian language.
  function getLithuanianForm(c) {
    if (c === 1 || (c % 10 === 1 && c % 100 > 20)) {
      return 0;
    } else if (Math.floor(c) !== c || (c % 10 >= 2 && c % 100 > 20) || (c % 10 >= 2 && c % 100 < 10)) {
      return 1;
    } else {
      return 2;
    }
  }

  // Internal helper function for Arabic language.
  function getArabicForm(c) {
    if (c <= 2) { return 0; }
    if (c > 2 && c < 11) { return 1; }
    return 0;
  }

  humanizeDuration.getSupportedLanguages = function getSupportedLanguages() {
    const result = [];
    for (const language in languages) {
      if (languages.hasOwnProperty(language) && language !== 'gr') {
        result.push(language);
      }
    }
    return result;
  };

  humanizeDuration.humanizer = humanizer;

  if (typeof define === 'function' && define.amd) {
    define(() => humanizeDuration);
  } else if (typeof module !== 'undefined' && module.exports) {
    module.exports = humanizeDuration;
  } else {
    this.humanizeDuration = humanizeDuration;
  }
}()); // eslint-disable-line semi
