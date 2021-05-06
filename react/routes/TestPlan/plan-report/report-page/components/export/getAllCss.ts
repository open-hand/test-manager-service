export default function getAllCss() {
  let result = '';
  const { styleSheets } = document;
  const { length } = styleSheets;
  for (let i = 0; i < length; i += 1) {
    const stylesheet = styleSheets[i];
    const { cssRules } = stylesheet;
    for (let j = 0; j < cssRules.length; j += 1) {
      const cssRule = cssRules[j];
      const { cssText } = cssRule;
      result = `${result} ${cssText}`;
    }
  }
  return result;
}
