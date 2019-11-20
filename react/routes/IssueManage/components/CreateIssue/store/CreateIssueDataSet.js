/* eslint-disable indent */
import { DataSet } from 'choerodon-ui/pro/lib';
import { stores } from '@choerodon/boot';
import { getProjectId } from '../../../../../common/utils';

const { AppState } = stores;
const linkOptions = new DataSet({
    selection: 'multiple',
    paging: false,
    autoQuery: true,
    fields: [
        { name: 'labelName', type: 'string' },
        { name: 'labelId', type: 'number' },
    ],
    transport: {
        read: {
            url: `agile/v1/projects/${AppState.currentMenuType.id}/issue_labels`,
            method: 'get',
            transformResponse(data) {
                return ({
                    ...JSON.parse(data),

                });
            },
        },
    },
});
function CreateIssueDataSet(intlPrefix, intl) {
    const summary = intl.formatMessage({ id: `${intlPrefix}_issueFilterBySummary` });
    const description = '描述';
    const folderId = intl.formatMessage({ id: `${intlPrefix}_folder` });
    const Issuelabel = intl.formatMessage({ id: 'summary_label' });

    return {
        autoQuery: false,
        selection: false,
        autoCreate: true,
        paging: false,
        dataKey: null,
        fields: [
            {
                name: 'summary', type: 'string', label: summary, required: true,
            },
            { name: 'description', type: 'string', label: description },
            {
                name: 'fileList',
                type: 'object',
                label: '附件',

            },
            {
                name: 'folder',
                type: 'object',
                label: folderId,
                required: true,
                textField: 'fileName',
                valueField: 'folderId',
                ignore: 'always',
            },
            {
                name: 'folderId',
                type: 'number',
                bind: 'folder.folderId',
            },
            // {
            //     name: 'versionId',
            //     type: 'number',
            //     bind: 'folder.versionId',
            //     ignore: 'never',
            // },
            {
                name: 'issueLink',
                type: 'object',
                label: Issuelabel,
                textField: 'labelName',
                valueField: 'labelId',
                multiple: ',',
                // options: linkOptions,
                lookupUrl: `agile/v1/projects/${AppState.currentMenuType.id}/issue_labels`,
            },
            {
                name: 'caseStepVOS',
                type: 'object',
            },

        ],

        transport: {
            // eslint-disable-next-line arrow-body-style
            submit: ({ data, dataSet }) => {
                // console.log('submit', data, dataSet);
                return ({
                    url: `/v1/projects/${getProjectId()}/case/create`,
                    method: 'post',
                    data: data[0],
                });
            },
        },
    };
}
export default CreateIssueDataSet;
