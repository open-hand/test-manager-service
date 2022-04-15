import useCategoryCodes from './useCategoryCodes';

const useIsWaterfall = () => {
  const codes = useCategoryCodes();
  const isWaterfall = codes.includes('N_WATERFALL');
  const isWaterfallAgile = codes.includes('N_WATERFALL_AGILE');
  return { isWaterfall, isWaterfallAgile };
};

export default useIsWaterfall;
