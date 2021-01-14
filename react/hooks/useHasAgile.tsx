import useCategoryCodes from './useCategoryCodes';

const useHasAgile = () => {
  const codes = useCategoryCodes();
  return codes.includes('N_AGILE');
};

export default useHasAgile;
