package io.choerodon.test.manager.api.dto;

import java.lang.reflect.Field;

import io.choerodon.agile.api.dto.IssueComponentDetailDTO;

/**
 * Created by zongw.lee@gmail.com on 09/05/2018
 */
public class IssueComponentDetailFolderRelDTO extends IssueComponentDetailDTO{
    private Long objectVersionNumber;

    public IssueComponentDetailFolderRelDTO(Long objectVersionNumber,IssueComponentDetailDTO issueComponentDetailDTO) throws NoSuchFieldException, IllegalAccessException {
        this.objectVersionNumber=objectVersionNumber;
        transferSelf(issueComponentDetailDTO);
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    private void transferSelf(IssueComponentDetailDTO issueComponentDetailDTO) throws IllegalAccessException, NoSuchFieldException {
        Class issueComponentDetailDTOClass = issueComponentDetailDTO.getClass();
        Field[] fields = issueComponentDetailDTOClass.getDeclaredFields();
        for(int i=0; i<fields.length; i++){
            Field f = fields[i];
            f.setAccessible(true);
            Field thisField = this.getClass().getSuperclass().getDeclaredField(f.getName());// 获取属性
            thisField.setAccessible(true);
            thisField.set(this, f.get(issueComponentDetailDTO));// 赋值
        }
    }
}
