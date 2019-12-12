import { useRef, useCallback } from 'react';
/**
 *
 *
 * @export
 * @param {*} fn (reset)=>()=>
 * @returns
 */
export default function useClickOnce(fn) {
  const clickRef = useRef(false);
  const reset = useCallback(() => {
    clickRef.current = false;
  }, []);
  const wrapper = useCallback(async (...args) => {
    if (!clickRef.current) {     
      clickRef.current = true; 
      await fn(reset)(...args);
    }
  }, [fn, reset]);  
  return wrapper;
}
