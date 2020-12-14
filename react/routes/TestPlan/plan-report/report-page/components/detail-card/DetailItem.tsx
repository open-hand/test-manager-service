import React from 'react';
import styles from './DetailItem.less';

export interface DetailItemProps {
  img: string
  title: string
  content: React.ReactNode
}

const DetailItem: React.FC<DetailItemProps> = ({ img, title, content }) => (
  <div className={styles.detail_item}>
    <div className={styles.left}>
      <img src={img} alt="" />
    </div>
    <div className={styles.right}>
      <div className={styles.title}>
        {title}
      </div>
      <div className={styles.content}>
        {content}
      </div>
    </div>
  </div>
);
export default DetailItem;
