/*
 * @Author: LainCarl 
 * @Date: 2019-01-17 14:29:21 
 * @Last Modified by: LainCarl
 * @Last Modified time: 2019-01-17 14:57:30
 * @Feature: 将某个store的属性动态注入到组件中，以避免父组件的渲染
 */

import { observer } from 'mobx-react';

const Injecter = observer(({ children, item, store }) => (Array.isArray(item) ? children(item.map(key => store[key])) : children(store[item])));

export default Injecter;
