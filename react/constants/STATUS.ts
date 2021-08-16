import { IStatus } from '@/common/types';

const STATUS: {
  [key in IStatus['valueCode']]: string
} = {
  todo: '#ffb100',
  doing: '#4d90fe',
  done: '#00bfa5',
  prepare: '#F67F5A',
};
export default STATUS;
