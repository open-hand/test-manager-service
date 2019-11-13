import { mutateTree } from '@atlaskit/tree';
import { useEffect, useRef } from 'react';

export function selectItem(tree, id, previous) {
  let newTree = tree;
  if (previous) {
    // const previousItem = tree.items[previous];
    newTree = mutateTree(newTree, previous, { selected: false });
  }
  // const item = tree.items[id];
  newTree = mutateTree(newTree, id, { selected: true });
  return newTree;
}
export const usePrevious = (value) => {
  const ref = useRef();
  useEffect(() => {
    ref.current = value;
  });
  return ref.current;
};
