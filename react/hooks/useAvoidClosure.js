import { useRef } from 'react';

export default function useAvoidClosure(fn) {
  const ref = useRef(fn);
  ref.current = fn;
  const callback = (...args) => ref.current(...args);
  return callback;
}
