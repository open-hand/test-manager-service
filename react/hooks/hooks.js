import { useRef, useCallback } from 'react';

function useAvoidClosure(fn) {
  const ref = useRef(fn);
  ref.current = fn;
  const callback = useCallback((...args) => {
    ref.current(...args);
  }, []);
  return callback;
}

export default useAvoidClosure;