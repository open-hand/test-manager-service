import Loadable from 'react-loadable';
import Loading from 'Loading';
const ResizeDivider = Loadable({
  loader: () => import('./Test'),
  loading: Loading
});
export default ResizeDivider;